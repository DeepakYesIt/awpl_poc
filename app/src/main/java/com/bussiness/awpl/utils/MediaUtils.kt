package com.bussiness.awpl.utils

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.util.TypedValue
import com.bussiness.awpl.adapter.SymptomAdapter
import com.bussiness.awpl.databinding.DialogMediaUploadBinding

class MediaUtils(
    context: Context,
    private val type: String,
    private val onFileSelected: (List<Uri>) -> Unit
) : Dialog(context) {

    private val selectedFiles = mutableListOf<Uri>()
    private var dialogAdapter: SymptomAdapter
    private var binding: DialogMediaUploadBinding = DialogMediaUploadBinding.inflate(LayoutInflater.from(context))

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set width with margin
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)
        window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Set description based on type
        binding.tvDescription.text = if (type == "video") "Add your videos here" else "Add your documents here"

        // Initialize adapter
        dialogAdapter = SymptomAdapter(selectedFiles) { fileUri -> removeFile(fileUri) }
        binding.mediaRecyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = dialogAdapter
        }

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnBrowseFile.setOnClickListener {
            dismiss()
            onFileSelected.invoke(selectedFiles)
        }

        binding.btnSave.setOnClickListener {
            if (selectedFiles.isNotEmpty()) {
                onFileSelected.invoke(selectedFiles)
                dismiss()
            }
        }

    }

    fun addFile(uri: Uri) {
        selectedFiles.add(uri)
        dialogAdapter.notifyItemInserted(selectedFiles.size - 1)
    }

    private fun removeFile(fileUri: Uri) {
        val position = selectedFiles.indexOf(fileUri)
        if (position != -1) {
            selectedFiles.removeAt(position)
            dialogAdapter.notifyItemRemoved(position)
            dialogAdapter.notifyItemRangeChanged(position, selectedFiles.size)
        }
    }
}
