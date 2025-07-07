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
import android.util.Log
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

    var fileUrl =""
    var date =""
    var doctorName =""

    var original_doctor_name =""
    var whenTime =""
    var new_doctor =""
    var new_date = ""
    var new_time =""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)



        intent?.let {

            if(it.hasExtra("original_doctor_name") &&it.hasExtra("when") && it.hasExtra("new_doctor")
                && it.hasExtra("new_date") && it.hasExtra("new_time")
                ){
                Log.d("TESTING_NAME",intent.getStringExtra("original_doctor_name").toString())
                Log.d("TESTING_NAME",intent.getStringExtra("when").toString())
                Log.d("TESTING_NAME",intent.getStringExtra("new_doctor").toString())
                Log.d("TESTING_NAME",intent.getStringExtra("new_date").toString())
                Log.d("TESTING_NAME",intent.getStringExtra("new_date").toString())


                original_doctor_name = intent.getStringExtra("original_doctor_name").toString()
                whenTime = intent.getStringExtra("when").toString()
                new_doctor =intent.getStringExtra("new_doctor").toString()
                new_date =  intent.getStringExtra("new_date").toString()
                new_time = intent.getStringExtra("new_time").toString()
            }

            if(it.hasExtra("fileUrl") && it.hasExtra("date")){
                 fileUrl = intent.getStringExtra("fileUrl").toString()
                 date = intent.getStringExtra("date").toString()

            }
            if(it.hasExtra("doctor_name") && it.hasExtra("date")){
                doctorName = intent.getStringExtra("doctor_name").toString()
                date = intent.getStringExtra("date").toString()
            }
        }
        setContentView(binding.root)

        // Apply language preferences
        sessionManager.applySavedLanguage()

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("TESTING_SESSION"," "+sessionManager.isLoggedIn())

            if (sessionManager.isLoggedIn()) {
                var intent = Intent(this, HomeActivity::class.java)
                if(fileUrl.isNotEmpty() && date.isNotEmpty()) {
                  intent.putExtra("fileUrl", fileUrl)
                  intent.putExtra("date", date)
               }
                else if(doctorName.isNotEmpty() && date.isNotEmpty()){
                    intent.putExtra("doctor_name", fileUrl)
                    intent.putExtra("date", date)
                }
                 else if(original_doctor_name.isNotEmpty() && whenTime.isNotEmpty() && new_doctor.isNotEmpty() &&new_date.isNotEmpty() &&
                    new_time.isNotEmpty()){
                    intent.putExtra("doctor_name", original_doctor_name)
                    intent.putExtra("when_time", whenTime)
                    intent.putExtra("new_doctor",new_doctor)
                    intent.putExtra("new_date",new_date)
                    intent.putExtra("new_time",new_time)
                }

                startActivity(intent)


            }
            else {
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
