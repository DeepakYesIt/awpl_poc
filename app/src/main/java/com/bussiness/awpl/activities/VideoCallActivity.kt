package com.bussiness.awpl.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.Button
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
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas


class VideoCallActivity : AppCompatActivity() {

    private lateinit var agoraEngine : RtcEngine
    private var isMuted = false
    private var isCameraOn = false
    //  private val appId = "40c77d114a684733ae30fcb9fcb0369c"
    private val appId = "1c45615e45194910baa3e4cad81a27fa"
    private val token = null
    private var channelName = "AWPL_Doctor_Web"

    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private var remoteUid: Int? = null
    private var isSwitched = false

    private var callStartTime: Long = 0L
    private var callTimerHandler: Handler? = null
    private var callTimerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    private fun startCallDurationTimer() {
        callStartTime = System.currentTimeMillis()
        callTimerHandler = Handler(Looper.getMainLooper())
        callTimerRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = System.currentTimeMillis() - callStartTime
                val seconds = (elapsedMillis / 1000) % 60
                val minutes = (elapsedMillis / (1000 * 60)) % 60
                val hours = (elapsedMillis / (1000 * 60 * 60))

                val timeString = String.format("Call : %02d:%02d:%02d", hours, minutes, seconds)
                runOnUiThread {
                    findViewById<TextView>(R.id.tv_timer)?.text = timeString
                }

                callTimerHandler?.postDelayed(this, 1000)
            }
        }
        callTimerHandler?.post(callTimerRunnable!!)
    }


    private fun hasPermissions() = PermissionsHelper.hasPermissions(this)
    private fun requestPermissions() = PermissionsHelper.requestPermissions(this)

    private fun stopCallDurationTimer() {
        callTimerHandler?.removeCallbacks(callTimerRunnable ?: return)
        callTimerHandler = null
        callTimerRunnable = null
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
            agoraEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), object : IRtcEngineEventHandler() {
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    Log.d("TESTING_NIKUNJ", "Join Success: $channel")
                }

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    runOnUiThread {
                        Log.d("TESTING_NIKUNJ", "Joined: $uid")
                        setupRemoteVideo(uid)
                        startCallDurationTimer()
                    }
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    runOnUiThread {
                        Log.d("TESTING_NIKUNJ", "offline $uid")
                        removeRemoteVideo()
                        stopCallDurationTimer()
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
        // Step 1: Create SurfaceView
        val surfaceView = SurfaceView(this).apply {
            setZOrderMediaOverlay(true) // important for local video overlay
        }

        // Step 2: Add it to your layout
        container.addView(surfaceView)

        // Step 3: Set it in Agora
        val videoCanvas = VideoCanvas(surfaceView, VideoCanvas.VIEW_SETUP_MODE_REPLACE, 0)
        agoraEngine.setupLocalVideo(videoCanvas)
        agoraEngine.startPreview()
    }

    private fun setupRemoteVideo(uid: Int) {

        val container = findViewById<FrameLayout>(R.id.remote_video_view)
        container.removeAllViews()

        val surfaceView = RtcEngine.CreateRendererView(applicationContext)
        surfaceView.setZOrderMediaOverlay(true)  // Optional: only if needed
        container.addView(surfaceView)

        val videoCanvas = VideoCanvas(surfaceView, VideoCanvas.VIEW_SETUP_MODE_REPLACE, uid)
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
        agoraEngine.joinChannel("007eJxTYHhZ2dL4aIXR3YQFWXlf/zrxrWD3uDL/7+uFrz6dd7I6cv+HAkNqsnFacpqZZZJlipmJiYVJonFKirFpkrmhRaqppbFl2rXlZhkNgYwMM4SPMTIyQCCIz8/gGB7gE++Sn1ySXxQfnprEwAAAWXMn3A=="
            , channelName, "", 0)
    }

    private fun leaveChannel() {
        agoraEngine.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
    }

    private fun setupUi() {

        var hideView = findViewById<FrameLayout>(R.id.local_video_view1)

        findViewById<ImageView>(R.id.btn_mute).setOnClickListener {
            isMuted = !isMuted
            agoraEngine.muteLocalAudioStream(isMuted)
       //  switchVideoViews()
        }

        findViewById<ImageView>(R.id.btn_switch_camera).setOnClickListener {
            var fmLay = findViewById<FrameLayout>(R.id.local_video_view)
            isCameraOn = !isCameraOn
            agoraEngine.muteLocalVideoStream(isCameraOn)
            if(isCameraOn) {
                fmLay.visibility = View.VISIBLE
            }else{
                fmLay.visibility =View.GONE
            }
        }

        findViewById<ImageView>(R.id.btn_end_call).setOnClickListener {
            agoraEngine.leaveChannel()
            finish()
        }
    }



}