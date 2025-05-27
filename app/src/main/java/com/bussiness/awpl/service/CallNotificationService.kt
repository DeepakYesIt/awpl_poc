package com.bussiness.awpl.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bussiness.awpl.AppContextProvider
import com.bussiness.awpl.CallNotificationActionReceiver
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.CallActivity
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.CallActionReceiver
import com.bussiness.awpl.utils.RingtoneHolder

import javax.annotation.Nullable


import android.app.*
import android.content.*
import android.media.*
import android.net.Uri
import android.os.*
import android.provider.Settings
import com.bussiness.awpl.activities.VideoCallActivity


import java.util.concurrent.TimeUnit
import java.util.*

 class CallNotificationService   : Service() {

     private var ringtone: Ringtone? = null

     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         val callerName = intent?.getStringExtra("caller_name") ?: "Unknown"
         showIncomingCallNotification(callerName)
         playRingtone()
         return START_NOT_STICKY
     }

     private fun showIncomingCallNotification(callerName: String) {
         val channelId = "incoming_call_channel"

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel = NotificationChannel(
                 channelId,
                 "Incoming Calls",
                 NotificationManager.IMPORTANCE_HIGH
             )
             channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
             getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
         }

         val fullScreenIntent = Intent(this, VideoCallActivity::class.java).apply {
             flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
             putExtra("caller_name", callerName)
         }

         val pendingIntent = PendingIntent.getActivity(
             this, 0, fullScreenIntent,
             PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
         )

         val notification = NotificationCompat.Builder(this, channelId)
             .setContentTitle("Incoming Call")
             .setContentText("Call from $callerName")
             .setSmallIcon(R.drawable.notification_icon)
             .setPriority(NotificationCompat.PRIORITY_HIGH)
             .setCategory(NotificationCompat.CATEGORY_CALL)
             .setFullScreenIntent(pendingIntent, true)
             .setOngoing(true)
             .build()

         startForeground(1001, notification)

         // Auto-stop service after 30 seconds if no action
         Handler(Looper.getMainLooper()).postDelayed({
             stopRingtone()
             stopSelf()
         }, 30_000)
     }

     private fun playRingtone() {
         val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
         ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
         ringtone?.play()
     }

     private fun stopRingtone() {
         ringtone?.stop()
     }

     override fun onDestroy() {
         super.onDestroy()
         stopRingtone()
     }

     override fun onBind(intent: Intent?): IBinder? = null
}