package com.bussiness.awpl

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.bussiness.awpl.databinding.DialogAppointRescheduleBinding
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.utils.DownloadWorker
import java.util.UUID

class DownloadRescheduleAppointment(private val activity: Activity,
                                    private val original_doctor_name: String,
                                    private val whenTime: String,
                                    private val new_doctor :String,
                                    private val new_date :String,
                                    private val new_time :String
) : Dialog(activity) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogAppointRescheduleBinding.inflate(activity.layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        // Width with margin
        val displayMetrics = activity.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)
        window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        var contentPrep = "Your appointment scheduled with Dr.${original_doctor_name} on ${whenTime} has been reassigned to Dr. ${new_doctor} due to unforeseen circumstances."
        // Set data
        binding.description2.text = contentPrep
        binding.dateTxt.text =" " +new_date
        binding.timeTxt.text = " "+new_time
        binding.doctorNameTxt.text = " "+new_doctor

        binding.btnYes.setOnClickListener {
            dismiss()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }

    }

}