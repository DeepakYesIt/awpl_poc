package com.bussiness.awpl

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bussiness.awpl.activities.VideoCallActivity
import com.bussiness.awpl.service.CallNotificationService
import com.bussiness.awpl.utils.CallActionReceiver
import com.bussiness.awpl.utils.RingtoneHolder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.

        // if (remoteMessage.data.containsKey("unread_booking_count")) {
//        var str = remoteMessage.data.get("unread_booking_count")
//        val intent = Intent("com.example.broadcast.ACTION_SEND_MESSAGE")
//        intent.putExtra("message", str.toString())
//        Log.d("BOOKING_COUNT", str.toString() + " Booking count is")
//        // Send the broadcast using LocalBroadcastManager
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent)
   // }

//        remoteMessage.data?.let {
//
//            val title = it.get("title")
//            val body = it.get("body")
//            if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
//                sendNotification(title, body)
//            } else {
//                Log.w("", "Notification data missing title or body: title=$title, body=$body")
//            }
//        }

        remoteMessage?.data?.let {
            val title = it.get("type")
            val body = it.get("channel_name")
            Log.d("TESTING_AWPL_NOTIFICATION",title +" "+ body)
            if(title == "incoming_call"){
                if (body != null) {
                    showIncomingCallNotification(AppContextProvider.getContext(),body)
                }
            }
        }


}
    private fun createCallNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CALL_CHANNEL",
                "Call Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming calls"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun showIncomingCallNotification(context: Context,channelName:String) {

//        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//        val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
//        ringtone.play()
//        RingtoneHolder.current = ringtone

        // Accept call broadcast

        val callerName = "Unknown"
        val callId = channelName

        createCallNotificationChannel()

        val intent = Intent(this, CallNotificationService::class.java).apply {
            putExtra("callerName", callerName)
            putExtra("callId", callId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

    }

    private fun createCallNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CALL_CHANNEL",
                "Incoming Call",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val channelId = "com.business.zyvo"
        /*    if(messageBody =="Your booking has been approved."*//*"Your booking  will start in 30 minutes."*//*){
             //startCountdownService()
           //  startCountTwo()
             val countdownDuration = 30 * 60 * 1000L
             val endTime = System.currentTimeMillis() + countdownDuration
             Log.d(ErrorDialog.TAG,"Yes"+messageBody)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.notification_icon)
                .setWhen(endTime)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setOngoing(true)
                .setContentText(messageBody)
                .setAutoCancel(true)

             val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
                 notificationManager.createNotificationChannel(channel)
             }

             var con= AppContextProvider.getContext()

             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 if (ContextCompat.checkSelfPermission(con, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                     notificationManager.notify(getRandomNumber(), notificationBuilder.build())
                 }
             }
             else{
                 notificationManager.notify(getRandomNumber(), notificationBuilder.build())
             }
         } else{*/
        val notificationBuilder = NotificationCompat.Builder(AppContextProvider.getContext(), channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentText(messageBody)
            .setAutoCancel(true)

        val notificationManager = AppContextProvider.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val con = AppContextProvider.getContext()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(con, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(getRandomNumber(), notificationBuilder.build())
            }
        } else {
            notificationManager.notify(getRandomNumber(), notificationBuilder.build())
        }
        //   }
    }

    fun getRandomNumber(): Int {
        val rand = Random()
        return rand.nextInt(1000)
    }

}