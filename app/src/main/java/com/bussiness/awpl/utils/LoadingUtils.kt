package com.bussiness.awpl.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bussiness.awpl.R
import com.bussiness.awpl.utils.JarvisLoader

 class LoadingUtils {

    companion object {

        private var jarvisLoader: JarvisLoader? = null

        fun showDialog(context: Context?, isCancelable: Boolean) {
            hideDialog()
            if (context != null) {
                try {
                    jarvisLoader = JarvisLoader(context)
                    jarvisLoader?.let { jarvisLoader ->
                        jarvisLoader.setCanceledOnTouchOutside(true)
                        jarvisLoader.setCancelable(isCancelable)
                        jarvisLoader.show()
                    }
                }       catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }



        fun hideDialog() {
            if (jarvisLoader != null && jarvisLoader?.isShowing!!) {
                jarvisLoader = try {
                    jarvisLoader?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }
        fun showSuccessDialog(context: Context?, text: String ,onDismiss: (() -> Unit)? = null) {

            if (context == null) return

            // Inflate the custom layout
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_success, null)

            // Find views
            val errorMessage = dialogView.findViewById<TextView>(R.id.text)
            val okButton = dialogView.findViewById<TextView>(R.id.textOkayButton)
            val cancelBtn = dialogView.findViewById<ImageView>(R.id.imageCross)

            // cancelBtn.visibility = View.GONE
            // Set the error message
            errorMessage.text =  ensurePeriod(text)

            // Create the dialog
            val dialog = AlertDialog.Builder(context).setView(dialogView).setCancelable(false).create()

            // Set button click listener
            okButton.setOnClickListener {
                dialog.dismiss()
                onDismiss?.invoke()
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
                onDismiss?.invoke()
            }

            // Show the dialog
            dialog.show()
        }



        fun showErrorDialog(context: Context?, text: String) {

            if (context == null) return

            // Inflate the custom layout
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_error, null)

            // Find views
            val errorMessage = dialogView.findViewById<TextView>(R.id.text)
            //  val errorIcon = dialogView.findViewById<ImageView>(R.id.errorIcon)
            val okButton = dialogView.findViewById<TextView>(R.id.textOkayButton)
            val cancelBtn = dialogView.findViewById<ImageView>(R.id.imageCross)

            // Set the error message
            errorMessage.text = ensurePeriod(text)

            // Create the dialog
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            // Add animation to the error icon
//            val animation = AnimationUtils.loadAnimation(context, R.anim.shake)
//            errorIcon.startAnimation(animation)


            cancelBtn.setOnClickListener {
                dialog?.dismiss()
            }

            // Set button click listener
            okButton.setOnClickListener {
                dialog?.dismiss()
            }

            // Show the dialog
            dialog?.show()
        }

        fun ensurePeriod(input: String): String {
            return if (input.endsWith(".")) input else "$input."
        }


    }

}