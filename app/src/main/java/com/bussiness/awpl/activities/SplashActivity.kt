package com.bussiness.awpl.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bussiness.awpl.databinding.ActivitySplashBinding
import com.bussiness.awpl.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Apply language preferences
        sessionManager.applySavedLanguage()

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (sessionManager.isLoggedIn()) {
//                startActivity(Intent(this, HomeActivity::class.java))
//            } else {
//                startActivity(Intent(this, OnBoardActivity::class.java))
//            }
//            finish()
//        }, 3000)

           Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                startActivity(Intent(this, VideoCallActivity::class.java))
            } else {
                startActivity(Intent(this, VideoCallActivity::class.java))
            }
            finish()
        }, 3000)

    }



}
