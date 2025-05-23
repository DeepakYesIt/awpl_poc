package com.bussiness.awpl.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters

class SimpleForegroundWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val channelId = "test_channel"
    private val notificationId = 123

    override suspend fun getForegroundInfo(): ForegroundInfo {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Foreground Worker")
            .setContentText("Notification from WorkManager")
            .setOngoing(true)
            .build()

        return ForegroundInfo(notificationId, notification)
    }

    override suspend fun doWork(): Result {
        // Just simulate work
        for (i in 1..5) {
            kotlinx.coroutines.delay(1000)
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Test Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
