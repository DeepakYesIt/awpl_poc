package com.bussiness.awpl

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bussiness.awpl.activities.DialogStartApointmentActivity
import com.bussiness.awpl.activities.DownloadDialogActivity
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.activities.RescheduleDialogActivity
import com.bussiness.awpl.activities.VideoCallActivity
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.service.CallNotificationService
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.CallActionReceiver
import com.bussiness.awpl.utils.DownloadWorker
import com.bussiness.awpl.utils.RingtoneHolder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.

        val data = remoteMessage.data

        data.let {
            var fileUrl:String? = null
            var date :String? =null
            val type = it["type"]

            if (type != null) {
                Log.d("testing_notification",type)
            }
            else{
                Log.d("testing_notification","Notificition is null")
            }
            if(type == "download_prescription"){
                Log.d("testing_notification",it.get("url") +" "+ it.get("date"))
                if(it.containsKey("url") && it.containsKey("date")) {
                    fileUrl = AppConstant.Base_URL + it.get("url")
                    date = it.get("date")
                    remoteMessage.notification?.title?.let { it1 ->
                        remoteMessage.notification?.body?.let { it2 ->
                            if (fileUrl != null) {
                                if (date != null) {
                                    callingFileDownload(
                                        fileUrl, date,
                                        it1, it2
                                    )
                                }
                            }
                        }
                    }

                } else {

                }
            }
            else if(type == "start_appoitment"){
                val doctorName = data["doctor_name"]
                val date = data["date"]

                Log.d("TESTING_PUSH_DATA", doctorName+" "+date)
                if (doctorName != null) {
                    Log.d("TESTING_NAME",doctorName)
                }
                if (isAppInForeground()) {
                    MyApp.currentActivity?.let { activity ->
                        activity.runOnUiThread {
                            doctorName?.let { it1 ->
                                    if (date != null) {
                                       var dialog = DialogStartAppointment(activity,
                                            it1, date)
                                        dialog.show()
                                    }
                                }


                        }

                    }
                    if (doctorName != null) {
                        showNotificationStartAppointment(doctorName , date ?: "")
                    } else {

                    }

                }
                else {

                    if (doctorName != null) {
                        Log.d("TESTING_NAME",doctorName)
                        showNotificationStartAppointment(doctorName , date ?: "")
                    } else {

                    }
                }
            }
            else if(type == "reschedule"){
                val originalDoctor = data["original_doctor_name"]
                val originalWhen = data["when"]
                val newDoctor = data["new_doctor"]
                val newDate = data["new_date"]
                val newTime = data["new_time"]

                if (isAppInForeground()) {
                    MyApp.currentActivity?.let { activity ->
                        activity.runOnUiThread {
                            val dialog = originalDoctor?.let { it1 ->
                                if (originalWhen != null) {
                                    if (newDoctor != null) {
                                        if (newTime != null) {
                                            if (newDate != null) {
                                                DownloadRescheduleAppointment(
                                                    activity,
                                                    it1,
                                                    originalWhen,
                                                    newDoctor,
                                                    newDate,
                                                    newTime
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }
                    showNotificationReschedule(originalDoctor ?: "", originalWhen ?: "", newDoctor ?: "", newDate ?: "", newTime ?: "")
                }
                else {
                    showNotificationReschedule(originalDoctor ?: "", originalWhen ?: "", newDoctor ?: "", newDate ?: "", newTime ?: "")
                }
            }
            else {

            }
        }

    }

    private fun showNotificationStartAppointment(doctorName: String, date: String) {
        val intent = Intent(this, DialogStartApointmentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_dialog", true)
            Log.d("TESTING_NAME",doctorName)
            putExtra("doctor_name", doctorName)
            putExtra("date", date)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "appointment_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel if Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Appointments",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon) // Replace with your icon
            .setContentTitle("Appointment with $doctorName")
            .setContentText("Scheduled on $date")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun isAppInForeground1(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = applicationContext.packageName

        for (process in appProcesses) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                process.processName == packageName) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun callingFileDownload(fileUrl: String, date :String, title:String, message: String){
        if (!fileUrl.isNullOrEmpty() && !date.isNullOrEmpty()) {
            if (isAppInForeground()) {
                // App is in foreground → open activity directly
//                val intent = Intent(this, DownloadDialogActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    putExtra("fileUrl", fileUrl)
//                    putExtra("date", date)
//                }
//                startActivity(intent)
                Log.d("testing_notification","I am inside the download notification foreground")
                MyApp.currentActivity?.let { activity ->
                    activity.runOnUiThread {
                        val dialog = DownloadDialog(activity, fileUrl, date)
                        dialog.show()
                    }
                }

            }
            else {
                Log.d("testing_notification","I am inside the download notification")
                showDownloadNotification(title, message, fileUrl, date)
            }
        }
    }



    private fun showDownloadNotification(
        title: String,
        message: String,
        fileUrl: String,
        date: String
    ) {
        val channelId = "download_channel_id"
        val notificationId = System.currentTimeMillis().toInt()

        // Intent to open activity when notification is clicked
        val intent = Intent(this, DownloadDialogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("fileUrl", fileUrl)
            putExtra("date", date)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "File Download Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for file download notifications"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon) // replace with your own icon
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }

    private fun showNotificationReschedule( originalDoctor: String,
                                            oldDateTime: String,
                                            newDoctor: String,
                                            newDate: String,
                                            newTime: String) {

        val intent = Intent(this, RescheduleDialogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_reschedule", true)
            putExtra("original_doctor_name", originalDoctor)
            putExtra("when", oldDateTime)
            putExtra("new_doctor", newDoctor)
            putExtra("new_date", newDate)
            putExtra("new_time", newTime)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "reschedule_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Reschedule Notifications", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Appointment Rescheduled")
            .setContentText("From $oldDateTime to $newDate at $newTime")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(200, notification)
    }

}
