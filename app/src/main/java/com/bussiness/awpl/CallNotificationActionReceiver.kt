package com.bussiness.awpl

import android.content.BroadcastReceiver
import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.bussiness.awpl.activities.CallActivity
import com.bussiness.awpl.activities.VideoCallActivity
import com.bussiness.awpl.service.CallNotificationService


class CallNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when(intent?.action) {
            "ACTION_ACCEPT_CALL" -> {
                Log.d("CallActionReceiver", "Call accepted")
                // Stop ringtone service
                context.stopService(Intent(context, CallNotificationService::class.java))

                // Launch call UI
                val callIntent = Intent(context, CallActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(callIntent)
            }
            "ACTION_DECLINE_CALL" -> {
                Log.d("CallActionReceiver", "Call declined")
                // Stop ringtone service
                context.stopService(Intent(context, CallNotificationService::class.java))
                // You can add more decline handling here if needed
            }
        }
    }
}