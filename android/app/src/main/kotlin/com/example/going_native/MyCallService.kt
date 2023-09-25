package com.example.going_native

import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class MyCallService : InCallService() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCallAdded(call: Call?) {
        OngoingCall.call = call
        MyCallActivity.start(this, call!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCallRemoved(call: Call?) {
        OngoingCall.call = null
    }
}