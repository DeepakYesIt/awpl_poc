package com.bussiness.awpl.activities

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.utils.DownloadWorker
import java.util.UUID

class DownloadDialogActivity : AppCompatActivity() {
    private var fileUrl :String =""
    private var date :String =""
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_download_dialog)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         fileUrl = intent.getStringExtra("fileUrl").toString()
         date = intent.getStringExtra("date").toString()
        downloadReportDialog()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadReportDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogReportDownloadBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        // Set width with margin
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)

        // Apply width to the dialog window
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.description4.text =date

        binding.btnOkay.setOnClickListener {
            dialog.dismiss()
            var intent = Intent(this,HomeActivity::class.java)
            DownloadWorker().downloadPdfWithNotification(this,fileUrl,"Presription/${UUID.randomUUID()}")
            Toast.makeText(this,"Download Started!",Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        binding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}