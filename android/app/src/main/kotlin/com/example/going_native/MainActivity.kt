package com.example.going_native

import android.Manifest.permission.CALL_PHONE
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.net.toUri
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {
    private lateinit var channel: MethodChannel

    @RequiresApi(VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION && PERMISSION_GRANTED in grantResults) {
            // makeTheActualCall()
            Log.i("PERM_RESULT", "onRequestPermissionsResult: ")
        }
    }

    @RequiresApi(VERSION_CODES.M)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)

        channel.setMethodCallHandler { call, result ->
            val phoneNumbers = (call.arguments) as List<*>
            if (call.method == "initiateCall") {
                initiateCall(phoneNumbers)
            } else {
                result.notImplemented()
            }
        }

    }

    @RequiresApi(VERSION_CODES.M)
    private fun initiateACall(phoneNumbers: List<*>) {
        val permission = ContextCompat.checkSelfPermission(this, CALL_PHONE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val uri = "tel:${phoneNumbers[1]}".toUri()
            startActivity(Intent(Intent.ACTION_CALL, uri))
        } else {
            requestPermissions(this, arrayOf(CALL_PHONE), REQUEST_PERMISSION)
            Log.i("initiateCall", "initiateCall: COULD NOT MAKE A CALL")
        }
    }

    @RequiresApi(VERSION_CODES.M)
    private fun initiateCall(phoneNumbers: List<*>) {
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                .let(::startActivity)
            Log.e("initiateCall", "No need to ask for Permissions")

        } else {
            initiateACall(phoneNumbers)
            println("phoneNumber 1: ${phoneNumbers[1]}")
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(
                Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }
        return batteryLevel
    }

    private fun batteryChannelMethodHandler(channel: MethodChannel) {
       //   Receive the data from Flutter
        channel.setMethodCallHandler { call, result ->
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()
                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 0
        const val MY_PHONE_NUMBER = "0787907590"
        const val CHANNEL = "askfield.com/battery"
    }
}
