package com.bussiness.awpl.service
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bussiness.awpl.R
import com.bussiness.awpl.utils.CallActionReceiver
import com.bussiness.awpl.utils.RingtoneHolder

class CallNotificationService : Service() {

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val callerName = intent?.getStringExtra("callerName") ?: "Unknown"
            val callId = intent?.getStringExtra("callId") ?: ""


            // Play ringtone
//            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//            ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
//            ringtone?.play()

            val acceptIntent = Intent(this, CallActionReceiver::class.java).apply {
                action = "ACTION_ACCEPT_CALL"
                putExtra("call_id", callId)
            }
            val acceptPendingIntent = PendingIntent.getBroadcast(
                this, 0, acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val declineIntent = Intent(this, CallActionReceiver::class.java).apply {
                action = "ACTION_DECLINE_CALL"
                putExtra("call_id", callId)
            }
            val declinePendingIntent = PendingIntent.getBroadcast(
                this, 1, declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, "CALL_CHANNEL")
                .setContentTitle("Incoming Call")
                .setContentText("Call from $callerName")
                .setSmallIcon(R.drawable.notification_icon)
                .addAction(R.drawable.notification_icon, "Accept", acceptPendingIntent)
                .addAction(R.drawable.notification_icon, "Decline", declinePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(acceptPendingIntent, true)
                .setOngoing(true)
                .build()

            startForeground(1001, notification)
            return START_NOT_STICKY
        }

        override fun onBind(intent: Intent?): IBinder? = null
}