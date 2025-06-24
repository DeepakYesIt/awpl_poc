package com.bussiness.awpl.utils

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
    private val onBrowseClicked: (() -> Unit)? = null,
    var buttonName : String ="Save"
) : Dialog(context) {

    private val selectedFiles = mutableListOf<Uri>()
    private var dialogAdapter: DialogMediaAdapter
    private var binding: DialogMediaUploadBinding = DialogMediaUploadBinding.inflate(LayoutInflater.from(context))
    private val MAX_FILE_LIMIT = 5

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

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
        binding.btnSave.text = buttonName

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

        val errorMessage = validateFileByTypeAndSize(context, uri)
        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
        else {
            if(!selectedFiles.contains(uri)){
                selectedFiles.add(uri)
                dialogAdapter.notifyItemInserted(selectedFiles.size - 1)
                updateVisibility()
            } else {
                Toast.makeText(context, "Already uploaded", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun validateFileByTypeAndSize(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri) ?: return "Unsupported file type"

        val sizeInBytes = getFileSize(context, uri) ?: return "Unable to determine file size"

        return when {
            type.startsWith("image/") -> {
                if (sizeInBytes <= 2L * 1024 * 1024) null
                else "Image size should be less than 2 MB"
            }
            type.startsWith("video/") -> {
                if (sizeInBytes <= 5L * 1024 * 1024) null
                else "Video size should be less than 5 MB"
            }
            type == "application/pdf" -> {
                if (sizeInBytes <= 10L * 1024 * 1024) null
                else "PDF size should be less than 10 MB"
            }
            else -> "Unsupported file type"
        }
    }

    private fun getFileSize(context: Context, uri: Uri): Long? {
        val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    return it.getLong(sizeIndex)
                }
            }
        }
        return null
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
