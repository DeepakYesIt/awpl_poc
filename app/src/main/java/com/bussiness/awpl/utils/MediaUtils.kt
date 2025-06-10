package com.bussiness.awpl.utils

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.bussiness.awpl.adapter.DialogMediaAdapter
import com.bussiness.awpl.databinding.DialogMediaUploadBinding

class MediaUtils(
    context: Context,
    type: String,
    private val onFileSelected: (List<Uri>) -> Unit,
    private val onBrowseClicked: (() -> Unit)? = null
) : Dialog(context) {

    private val selectedFiles = mutableListOf<Uri>()
    private var dialogAdapter: DialogMediaAdapter
    private var binding: DialogMediaUploadBinding =
        DialogMediaUploadBinding.inflate(LayoutInflater.from(context))

    private val MAX_FILE_LIMIT = 5

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
        binding.tvTitle.text = if (type == "PDF") "Upload PDF" else "Media Upload"

        // Initialize adapter
        dialogAdapter = DialogMediaAdapter(selectedFiles, type) { fileUri -> removeFile(fileUri) }
        binding.mediaRecyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = dialogAdapter
        }

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnBrowseFile.setOnClickListener {
            if (selectedFiles.size >= MAX_FILE_LIMIT) {
                Toast.makeText(context, "You can only upload up to $MAX_FILE_LIMIT items.", Toast.LENGTH_SHORT).show()
            } else {
                onBrowseClicked?.invoke()
            }
        }

        binding.btnSave.setOnClickListener {
            if (selectedFiles.isNotEmpty()) {
                onFileSelected(selectedFiles)
                dismiss()
            }
        }

        updateVisibility()
    }

    fun handleSelectedFile(uri: Uri) {
        if (selectedFiles.size >= MAX_FILE_LIMIT) {
            Toast.makeText(context, "Maximum of $MAX_FILE_LIMIT files allowed.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!selectedFiles.contains(uri)) {
            selectedFiles.add(uri)
            dialogAdapter.notifyItemInserted(selectedFiles.size - 1)
            updateVisibility()
        }
    }


    fun addFiles(newFiles: List<Uri>) {
        val total = selectedFiles.size + newFiles.size
        if (total > MAX_FILE_LIMIT) {
            Toast.makeText(context, "Only ${MAX_FILE_LIMIT - selectedFiles.size} more files can be added.", Toast.LENGTH_SHORT).show()
            return
        }
        val filteredFiles = newFiles.filter { !selectedFiles.contains(it) }
        selectedFiles.addAll(filteredFiles)
        dialogAdapter.notifyItemRangeInserted(selectedFiles.size - filteredFiles.size, filteredFiles.size)
        updateVisibility()
    }


    private fun updateVisibility() {
        binding.mediaRecyclerView.visibility = if (selectedFiles.isNotEmpty()) View.VISIBLE else View.GONE
        binding.btnSave.visibility = if (selectedFiles.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun removeFile(fileUri: Uri) {
        val position = selectedFiles.indexOf(fileUri)
        if (position != -1) {
            selectedFiles.removeAt(position)
            dialogAdapter.notifyItemRemoved(position)
            dialogAdapter.notifyItemRangeChanged(position, selectedFiles.size)
            updateVisibility()
        }
    }
}
