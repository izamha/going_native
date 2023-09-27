package com.example.going_native

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.Locale
import java.util.concurrent.TimeUnit


class MyCallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    private lateinit var callInfo: TextView
    private lateinit var answer: Button
    private lateinit var hangup: Button
    private lateinit var questionaire: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
//        moveTaskToBack(true)
        answer = findViewById(R.id.answer)
        hangup = findViewById(R.id.hangup)
        callInfo = findViewById(R.id.callInfo)
        questionaire = findViewById(R.id.btn_questionaire)

        number = intent.data?.schemeSpecificPart.toString()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            answer.setOnClickListener {
                OngoingCall.answer()
            }
            hangup.setOnClickListener {
                OngoingCall.hangup()
            }

            questionaire.setOnClickListener {
                val questionaireIntent = Intent(
                    this,
                    QuestionaireActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(questionaireIntent)
            }

            OngoingCall.state
                .subscribe(::updateUi)
                .addTo(disposables)

            OngoingCall.state
                .filter { it == Call.STATE_DISCONNECTED }
                .delay(1, TimeUnit.SECONDS)
                .firstElement()
                .subscribe { finish() }
                .addTo(disposables)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            callInfo.text =
                "${state.asString().lowercase(Locale.ROOT).uppercase(Locale.ROOT)}\n\n$number"
            answer.isVisible = state == Call.STATE_RINGING
            hangup.isVisible = state in listOf(
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE
            )
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.M)
        fun start(context: Context, call: Call) {
            Intent(context, MyCallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }
}