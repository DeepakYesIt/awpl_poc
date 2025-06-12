package com.bussiness.awpl

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.utils.DownloadWorker
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.Q)
class DownloadDialog(private val activity: Activity,
                     private val fileUrl: String,
                     private val date: String
) : Dialog(activity) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogReportDownloadBinding.inflate(activity.layoutInflater)
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
        binding.description4.text = date

        binding.btnOkay.setOnClickListener {
            dismiss()
            DownloadWorker().downloadPdfWithNotification(activity, fileUrl, "Prescription/${UUID.randomUUID()}")
            Toast.makeText(activity, "Download Started!", Toast.LENGTH_LONG).show()
            activity.startActivity(Intent(activity, HomeActivity::class.java))
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

}