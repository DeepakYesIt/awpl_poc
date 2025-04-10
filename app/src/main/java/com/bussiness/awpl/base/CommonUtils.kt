package com.bussiness.awpl.base

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.OnBoardActivity
import com.bussiness.awpl.databinding.DialogLogoutBinding
import com.bussiness.awpl.utils.SessionManager

object CommonUtils {

    private var dialog: Dialog? = null
    private val handler = Handler(Looper.getMainLooper())

    private val images = arrayOf(
        R.drawable.sth_scope,
        R.drawable.inj_ic,
        R.drawable.cps_ic,
        R.drawable.case_ic
    )

    private var imageIndex = 0

    private val imageChanger = object : Runnable {
        override fun run() {
            dialog?.findViewById<ImageView>(R.id.loaderImageView)?.setImageResource(images[imageIndex])
            imageIndex = (imageIndex + 1) % images.size
            handler.postDelayed(this, 400)
        }
    }

    fun showLoader(context: Context) {
        if (dialog != null && dialog!!.isShowing) return

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loader_layout)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Rotate progress bar
            ObjectAnimator.ofFloat(findViewById<ProgressBar>(R.id.circularProgressBar), "rotation", 0f, 360f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                start()
            }
        }

        dialog?.show()
        handler.post(imageChanger)
    }

    fun hideLoader() {
        handler.removeCallbacks(imageChanger)
        dialog?.dismiss()
        dialog = null
    }

    fun logoutDialog(context: Context) {
        val dialog = Dialog(context)
        val binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNo.setOnClickListener { dialog.dismiss() }
            btnYes.setOnClickListener {
                dialog.dismiss()

                val sessionManager = SessionManager(context)
                sessionManager.clearSession()

                val intent = Intent(context, OnBoardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("LOAD_FRAGMENT", "login")
                }
                context.startActivity(intent)
            }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)

            show()
        }
    }
}
