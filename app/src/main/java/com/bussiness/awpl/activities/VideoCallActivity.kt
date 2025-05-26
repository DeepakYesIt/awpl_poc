package com.bussiness.awpl.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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
    private var isCameraOn = true
  //  private val appId = "40c77d114a684733ae30fcb9fcb0369c"
    private val appId = "1c45615e45194910baa3e4cad81a27fa"
    private val token = null
    private var channelName = "Doctor_Web"

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


    private fun hasPermissions() = PermissionsHelper.hasPermissions(this)
    private fun requestPermissions() = PermissionsHelper.requestPermissions(this)

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
                        setupRemoteVideo(uid) }
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

//        val view = RtcEngine.createRendererView(applicationContext)
//        view.setZOrderMediaOverlay(true)
//        mLocalContainer.addView(view)
//
//        mLocalVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0)
//        mRtcEngine?.setupLocalVideo(mLocalVideo)

//        val container = findViewById<FrameLayout>(R.id.remote_video_view)
//        container.removeAllViews()
//
//        val surfaceView = SurfaceView(this)
//
//        container.addView(surfaceView)
//
//        val videoCanvas = VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid)
//        agoraEngine.setupRemoteVideo(videoCanvas)


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
        agoraEngine.joinChannel("007eJxTYLipcSeTZ4nZ4TclToJqrrln9279XiuavjnTbPPbDzXmgkEKDImGiQbGycmm5qbGxibGFsYWicYpJolGpmaJyYkWBibJmXkmGQ2BjAxfn7IxMEIhiM/F4JKfXJJfFB+emsTAAAABESFK"
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
        }

        findViewById<ImageView>(R.id.btn_switch_camera).setOnClickListener {
            var fmLay = findViewById<FrameLayout>(R.id.local_video_view)
            isCameraOn = !isCameraOn
            agoraEngine.muteLocalVideoStream(isCameraOn)
//

        }

        findViewById<ImageView>(R.id.btn_end_call).setOnClickListener {
          agoraEngine.leaveChannel()
            finish()
        }
    }

}