package com.bussiness.awpl

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.DialogReminderBinding
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.utils.DownloadWorker
import java.util.UUID

class DialogStartAppointment(
    private val activity: Activity,
    private val doctor_name: String,
    private val date: String
) : Dialog(activity){

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogReminderBinding.inflate(activity.layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        // Width with margin
        val displayMetrics = activity.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)
        window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Set data
        binding.dateTimeTxt.text = date
        Log.d("TESTING_NAME",doctor_name+"inside dialog")

        binding.tvDocName.text ="Doctor Name: "+ doctor_name

        binding.btnYes.setOnClickListener {
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

    }

}