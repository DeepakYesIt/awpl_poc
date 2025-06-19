package com.bussiness.awpl.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.SurfaceHolder
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bussiness.awpl.PermissionsHelper
import com.bussiness.awpl.R
import com.bussiness.awpl.utils.AppConstant
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class VideoCallActivity : AppCompatActivity() {

    private var callStartTimeMillis: Long = 0L
    private lateinit var agoraEngine : RtcEngine
    private var isMuted = false
    private var isCameraOn = true
    private var appId = "40c77d114a684733ae30fcb9fcb0369c"
     // private var appId = "1c45615e45194910baa3e4cad81a27fa"
    private var token: String? = null
    private var channelName = "AWPL_Doctor_Web"

    private var remoteSurfaceView: SurfaceView? = null
    private var remoteUid: Int? = null
    private var isSwitched = false
    private var doctorName :String = ""

    private var startTimeCall :String =""
    private var callStartTime: Long = 0L
    var uid :Int =0
    private var callTimerHandler: Handler? = null
    private var callTimerRunnable: Runnable? = null
    private var frontOpen = true

    private var countdownHandler: Handler? = null
    private var countdownRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


//        appId = getString(R.string.agora_app_id)
//        channelName ="nikunj"
//        token = null
//
         doctorName = intent.getStringExtra(AppConstant.DOCTOR)?.takeIf { it.isNotBlank() }?:""
         appId = intent.getStringExtra(AppConstant.APPID)?.takeIf { it.isNotBlank() } ?: ""
         token = intent.getStringExtra(AppConstant.AuthToken) ?: ""
         channelName = intent.getStringExtra(AppConstant.CHANNEL_NAME)?.takeIf { it.isNotBlank() } ?: ""
         uid = intent.getIntExtra(AppConstant.uid, 0)
        startTimeCall = intent.getStringExtra(AppConstant.TIME).toString()
        var doctorTv = findViewById<TextView>(R.id.tv_doctor)
        doctorTv.setText(doctorName)
        startElapsedCountdownFromStartTime(startTimeCall)
        try {
            Log.d("TESTING_VIDEO_LOG","appid "+appId)
            Log.d("TESTING_VIDEO_LOG","appid "+token)
            Log.d("TESTING_VIDEO_LOG","channelName"+channelName)
            Log.d("TESTING_TIME",startTimeCall.toString())
        }catch (e:Exception){

        }


        if (hasPermissions()) {
            Log.d("TESTING_NIKUNJ", "Inside oncreate")
            initAgora()

        }
        else {
            requestPermissions()
        }

        setupUi()

    }

    private fun hasPermissions() = PermissionsHelper.hasPermissions(this)
    private fun requestPermissions() = PermissionsHelper.requestPermissions(this)


    private fun startElapsedCountdownFromStartTime(timeRange: String) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        formatter.isLenient = false
        try {
            val parts = timeRange.trim().split("-")
            if (parts.size != 2) throw IllegalArgumentException("Invalid time range format")
            val startTimeStr = parts[0].trim() + " " + parts[1].trim().takeLast(2) // e.g., "06:00 PM"
            val startTime = formatter.parse(startTimeStr) ?: throw Exception("Invalid start time")

            // Set the start time to today's date
            val now = Calendar.getInstance()
            val startCalendar = Calendar.getInstance().apply {
                time = startTime
                set(Calendar.YEAR, now.get(Calendar.YEAR))
                set(Calendar.MONTH, now.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            }

            val startTimeMillis = startCalendar.timeInMillis

            countdownHandler = Handler(Looper.getMainLooper())
            countdownRunnable = object : Runnable {
                override fun run() {
                    val elapsedMillis = System.currentTimeMillis() - startTimeMillis
                    val clampedElapsed = maxOf(0L, elapsedMillis)

                    val minutes = (clampedElapsed / (1000 * 60))
                    val seconds = (clampedElapsed / 1000) % 60

                    val timeString = String.format("Elapsed: %02d:%02d", minutes, seconds)
                    if(timeString == "Elapsed: 15:00"){
                        Toast.makeText(this@VideoCallActivity,
                            "Your appointment time is over,will be disconnected automatically after 5 minutes.",
                            Toast.LENGTH_LONG).show()
                    }
                    Log.d("TESTING_USER_TIME",timeString)
                    findViewById<TextView>(R.id.tv_timer)?.text = timeString

                    countdownHandler?.postDelayed(this, 1000)
                }
            }
            countdownHandler?.post(countdownRunnable!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
        }
    }
    private fun stopElapsedCountdown() {
        countdownHandler?.removeCallbacks(countdownRunnable!!)
        countdownHandler = null
        countdownRunnable = null
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionsHelper.handleResult(requestCode, grantResults)) {
            initAgora()
        } else {
            Toast.makeText(this, "Permissions required", Toast.LENGTH_LONG).show()
            if (PermissionsHelper.shouldShowRationale(this)) {
                requestPermissions()
            } else {
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName"))
                )
            }
        }
    }

    private fun initAgora() {
        try {
            agoraEngine = RtcEngine.create(baseContext, appId, object : IRtcEngineEventHandler() {
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    Log.d("TESTING_NIKUNJ", "Join Success: $channel")
                }

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    runOnUiThread {
                        Log.d("TESTING_NIKUNJ", "Joined: $uid")

                        setupRemoteVideo(uid)

                        Log.d("TESTING_TIME",startTimeCall.toString())
                     //   startCallDurationTimer(startTimeCall)
                        startElapsedCountdownFromStartTime(startTimeCall)
                    }
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    runOnUiThread {
                        Log.d("TESTING_NIKUNJ", "offline $uid")
                        removeRemoteVideo()

                        agoraEngine.leaveChannel()
                        // Finish the call screen
                        finish()
                    }
                }

                override fun onRemoteVideoStateChanged(
                    uid: Int,
                    state: Int,
                    reason: Int,
                    elapsed: Int
                ) {
                    super.onRemoteVideoStateChanged(uid, state, reason, elapsed)

                    if (state == Constants.REMOTE_VIDEO_STATE_STOPPED || reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED) {
                        // Hide the remote video view or show blank screen
                        runOnUiThread {
                            showBlankScreenForUser()
                        }

                    } else  {
                        // Remote video resumed
                        runOnUiThread {
                            setupRemoteVideo(uid)
                        }
                    }
                }
                override fun onError(err: Int) {
                    super.onError(err)
                    Log.d("TESTING_NIKUNJ", "Inside Error" +err)
                }
            })
            setupLocalVideo()
            joinChannel()
        } catch (e: Exception) {
            Log.e("AGORA", "Init error: $e")
        }
    }

    private fun showBlankScreenForUser(){
        val remoteContainer = findViewById<FrameLayout>(R.id.remote_video_view)
        remoteContainer.removeAllViews()
        val blankView = View(this)
        blankView.setBackgroundColor(Color.BLACK)
        remoteContainer.addView(blankView)
    }

    private fun setupLocalVideo() {
        val container = findViewById<FrameLayout>(R.id.local_video_view)
        val surfaceView = SurfaceView(this).apply {
            setZOrderMediaOverlay(true) // important for local video overlay
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        container.removeAllViews()
        container.addView(surfaceView)
        val videoCanvas = VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN , 0)
        agoraEngine.setupLocalVideo(videoCanvas)
        agoraEngine.startPreview()
    }

    //olde code
    private fun setupRemoteVideo(uid: Int) {

        val container = findViewById<FrameLayout>(R.id.remote_video_view)
        container.removeAllViews()

        val surfaceView = RtcEngine.CreateRendererView(applicationContext)
        surfaceView.setZOrderMediaOverlay(true)  // Optional: only if needed
        container.addView(surfaceView)

        val videoCanvas = VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        agoraEngine?.setupRemoteVideo(videoCanvas)
        setupLocalVideo()

    }

    private fun removeRemoteVideo() {
        Log.d("TESTING_VIDEO_CALL","ON REMOVING VIDEO CALL")
        findViewById<FrameLayout>(R.id.remote_video_view).removeAllViews()
    }


    private fun joinChannel() {

        agoraEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)

        agoraEngine.enableVideo()

        agoraEngine.joinChannel(token,
            channelName,
            "",
            0)
    }

    private fun leaveChannel() {
        agoraEngine.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
        stopElapsedCountdown()
    }

    private fun setupUi() {

        var hideView = findViewById<FrameLayout>(R.id.local_video_view1)

        var imgBtn = findViewById<ImageView>(R.id.btn_mute)
        findViewById<ImageView>(R.id.btn_mute).setOnClickListener {
            isMuted = !isMuted
            if(isMuted){
                imgBtn.setImageResource(R.drawable.ic_audio_mute)
            }else{
                imgBtn.setImageResource(R.drawable.ic_audio)
            }
            agoraEngine.muteLocalAudioStream(isMuted)
       //  switchVideoViews()
        }

        var switchCamera = findViewById<ImageView>(R.id.btn_switch_camera)
        var switchCameraToBackFront = findViewById<ImageView>(R.id.switch_icon)
        findViewById<ImageView>(R.id.btn_switch_camera).setOnClickListener {
            var fmLay = findViewById<FrameLayout>(R.id.local_video_view)
            isCameraOn = !isCameraOn
            agoraEngine.muteLocalVideoStream(isCameraOn)
            if(isCameraOn) {
                switchCamera.setImageResource(R.drawable.ic_video_mute)
                fmLay.visibility =View.GONE
                switchCameraToBackFront.visibility = View.GONE
            }else{
                switchCamera.setImageResource(R.drawable.ic_video)
                fmLay.visibility = View.VISIBLE
                switchCameraToBackFront.visibility = View.VISIBLE
            }
        }

        findViewById<ImageView>(R.id.btn_end_call).setOnClickListener {
            agoraEngine.leaveChannel()
            finish()
        }
        switchCameraToBackFront.setOnClickListener {
            frontOpen = !frontOpen
            agoraEngine.switchCamera()
        }
    }



}