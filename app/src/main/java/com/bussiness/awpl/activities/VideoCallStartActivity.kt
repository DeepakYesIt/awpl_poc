package com.bussiness.awpl.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.fragment.home.HomeViewModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.utils.VideoCallCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder

@AndroidEntryPoint
class VideoCallStartActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_call_start)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val appointmentId = intent.getStringExtra("appointmentId")
        val doctorName = intent.getStringExtra("doctor")
        val token = intent.getStringExtra("token")
        val time = intent.getStringExtra("time")
        val date = intent.getStringExtra("date")

        val decodedSlot = URLDecoder.decode(time, "UTF-8")
        val decodedSlotDoctorName = URLDecoder.decode(doctorName, "UTF-8")
        val dateDecode = URLDecoder.decode(date,"UTF-8")
        val tokendecode =  token?.let {
            String(Base64.decode(it, Base64.URL_SAFE or Base64.NO_WRAP))
        }

        if(!SessionManager(this).isMoreThanFiveMinutesPastStartTime(dateDecode, decodedSlot)) {
            Log.d(
                "TEST_TIMING", decodedSlot.toString() + "," +
                        appointmentId.toString() + "," + tokendecode.toString() + " " + decodedSlotDoctorName + " " + dateDecode
            )

            if (SessionManager(this).isLoggedIn() && SessionManager(this).getAuthToken()!= tokendecode) {
                SessionManager(this).clearSession()
            }
            if (tokendecode != null) {
                SessionManager(this).setAuthToken(tokendecode)

            }
            SessionManager(this).setTime(decodedSlot)
            SessionManager(this).setDate(dateDecode)
            appointmentId?.toInt()?.let {
                if (doctorName != null) {
                    if (time != null) {
                        val decodedSlot = URLDecoder.decode(time, "UTF-8")
                        val decodedSlotDoctorName = URLDecoder.decode(doctorName, "UTF-8")
                        createChannel(it, decodedSlotDoctorName, decodedSlot)
                    }
                }
            }
            Log.d("VideoCall", "Appointment ID: $appointmentId")
            Log.d("VideoCall", "Token: $token")
            resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data = result.data
                        var timeStr = SessionManager(this).getTime()
                        var dateStr = SessionManager(this).getDate()
                        if (SessionManager(this).shouldShow(dateStr, timeStr)) {

                           if(!SessionManager(this@VideoCallStartActivity).
                               isListPresent(SessionManager(this@VideoCallStartActivity).getAppointment().toString())
                               ) {
                               showRatingDialog()
                           }
                        } else {
                            finish()
                        }


                    }

                }
        }
        else {
            LoadingUtils.showSuccessDialog(this,"Appointment Time is Over"){
                finish()

            }

        }
    }

    private fun createChannel(startAppointment: Int,doctorName:String,time:String) {

        this.lifecycleScope.launch {
            LoadingUtils.showDialog(this@VideoCallStartActivity,false)

            homeViewModel.createChannel(startAppointment).collect {
                when(it){
                    is NetworkResult.Success ->{
                        it.data?.let {
                            val intent = Intent(this@VideoCallStartActivity, VideoCallActivity::class.java)
                            intent.putExtra(AppConstant.APPID, it.appId)
                            intent.putExtra(AppConstant.AuthToken, it.token)
                            intent.putExtra(AppConstant.CHANNEL_NAME, it.channelName)
                            intent.putExtra(AppConstant.uid, it.uid)
                            intent.putExtra(AppConstant.DOCTOR, doctorName)
                            intent.putExtra(AppConstant.TIME,time)
                            callingCallJoinedApi(startAppointment,intent)
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(this@VideoCallStartActivity,it.message.toString())
                    }
                    else ->{

                    }
                }
            }
        }

    }

    private fun callingCallJoinedApi(startAppointment: Int, intent: Intent) {
        this.lifecycleScope.launch{
            homeViewModel.callJoined(startAppointment).collect  {
                when(it){
                    is NetworkResult.Success ->{

                        SessionManager(this@VideoCallStartActivity).setAppointmentId(startAppointment)

                        VideoCallCheck.checkAndJoinAppointmentCall(this@VideoCallStartActivity,startAppointment.toString()){
                            LoadingUtils.hideDialog()
                               resultLauncher.launch(intent)
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                    }
                    else ->{
                    }
                }
            }
        }
    }

    private fun showRatingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rating_review, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBarSmall)
        val etReview = dialogView.findViewById<EditText>(R.id.et_review)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btn_submit)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val reviewText = etReview.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                   LoadingUtils.showDialog(this@VideoCallStartActivity,false)
                    homeViewModel.submitFeedBack(
                        SessionManager(this@VideoCallStartActivity).getAppointment(),
                        rating.toInt(),
                        reviewText
                    ).collect {
                        when(it){
                            is NetworkResult.Success ->{
                                LoadingUtils.hideDialog()
                                var oldList = SessionManager(this@VideoCallStartActivity).getStringList(AppConstant.FEEDBACK)
                                oldList.add( SessionManager(this@VideoCallStartActivity).getAppointment().toString())
                                SessionManager(this@VideoCallStartActivity).saveStringList(AppConstant.FEEDBACK, oldList)

                                dialog.dismiss()
                                LoadingUtils.showSuccessDialog(this@VideoCallStartActivity,it.data.toString()){
                                    finish()
                                }

                            }

                            is NetworkResult.Error ->{
                                LoadingUtils.hideDialog()
                                dialog.dismiss()
                                finish()
                            }
                            else ->{
                            }
                        }

                    }

                }


                // submitReview(rating, reviewText)
            }
        }

        dialog.show()
    }


}