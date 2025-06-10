package com.bussiness.awpl.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.OutputStream
import java.util.Random

class DownloadWorker  {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadPdfWithNotification(context: Context, url: String, fileName: String) {
        val channelId = "pdf_download_channel"
        val notificationId = Random().nextInt(10000)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PDF Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading...")
            .setContentText(fileName)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(notificationId, builder.build())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val body = response.body ?: throw IOException("Empty response")

                val total = body.contentLength()
                var downloaded = 0L

                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("Failed to create file in MediaStore")

                resolver.openOutputStream(uri).use { outputStream ->
                    val inputStream = body.byteStream()
                    val buffer = ByteArray(8192)
                    var read: Int

                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream?.write(buffer, 0, read)
                        downloaded += read
                        val progress = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                        builder.setProgress(100, progress, false)
                        notificationManager.notify(notificationId, builder.build())
                    }

                    outputStream?.flush()
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                // Intent to view the PDF
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    viewIntent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    else PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Update notification
                builder.setContentTitle("Download Complete")
                    .setContentText("Tap to open $fileName")
                    .setProgress(0, 0, false)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(false)

                notificationManager.notify(notificationId, builder.build())

            } catch (e: Exception) {
                e.printStackTrace()
                builder.setContentTitle("Download Failed")
                    .setContentText(e.localizedMessage ?: "Unknown error")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(notificationId, builder.build())
            }
        }


    }
}
