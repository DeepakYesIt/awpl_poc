package com.bussiness.awpl.activities


import okhttp3.Request
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bussiness.awpl.databinding.ActivitySplashBinding
import com.bussiness.awpl.utils.DownloadWorker
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.utils.SimpleForegroundWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.Random

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Apply language preferences
        sessionManager.applySavedLanguage()

        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, OnBoardActivity::class.java))
            }
            finish()
        }, 3000)

//           Handler(Looper.getMainLooper()).postDelayed({
//            if (sessionManager.isLoggedIn()) {
//                startActivity(Intent(this, VideoCallActivity::class.java))
//            } else {
//                startActivity(Intent(this, VideoCallActivity::class.java))
//            }
//            finish()
//        }, 3000)

    }



}
