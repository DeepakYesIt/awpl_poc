package com.bussiness.awpl.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bussiness.awpl.activities.VideoCallActivity

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "ACTION_ACCEPT_CALL" -> {
                RingtoneHolder.current?.stop()
                val callId = intent.getStringExtra("call_id") ?: "N/A"
                val callIntent = Intent(context, VideoCallActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("caller_name", callId)
                }
                context.startActivity(callIntent)

                notificationManager.cancel(1001)
            }

            "ACTION_DECLINE_CALL" -> {
                RingtoneHolder.current?.stop()
                notificationManager.cancel(1001)
            }
        }
    }
}