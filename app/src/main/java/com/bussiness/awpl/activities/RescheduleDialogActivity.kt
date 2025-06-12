package com.bussiness.awpl.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bussiness.awpl.R

class RescheduleDialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reschedule_dialog)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("open_reschedule", false) == true) {
//            showRescheduleDialog(
//                intent.getStringExtra("original_doctor_name") ?: "",
//                intent.getStringExtra("when") ?: "",
//                intent.getStringExtra("new_doctor") ?: "",
//                intent.getStringExtra("new_date") ?: "",
//                intent.getStringExtra("new_time") ?: ""
//            )
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver, IntentFilter("SHOW_RESCHEDULE_DIALOG")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            showRescheduleDialog(
//                intent?.getStringExtra("original_doctor_name") ?: "",
//                intent?.getStringExtra("when") ?: "",
//                intent?.getStringExtra("new_doctor") ?: "",
//                intent?.getStringExtra("new_date") ?: "",
//                intent?.getStringExtra("new_time") ?: ""
//            )
        }
    }

}