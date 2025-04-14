package com.bussiness.awpl.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemSelectedFileBinding

class DialogMediaAdapter(
    private val files: MutableList<Uri>,
    private val type: String,
    private val onRemove: (Uri) -> Unit
) : RecyclerView.Adapter<DialogMediaAdapter.FileViewHolder>() {

    inner class FileViewHolder(private val binding: ItemSelectedFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fileUri: Uri) {
            binding.apply {
                imageText.text = fileUri.lastPathSegment ?: "Unknown File"
                imageSizeText.text = "5.3MB"  // TODO: Replace with actual file size logic

                when (type) {
                    "video" -> imageView.setImageResource(R.drawable.video_svg)
                    "pdf" -> imageView.setImageResource(R.drawable.pdf_ic)
                    else -> imageView.setImageResource(R.drawable.imageview_icon)
                }

                crossBtn.setOnClickListener {
                    onRemove(fileUri)
                    removeItem(adapterPosition)  // Remove from list dynamically
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemSelectedFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size

    fun removeItem(position: Int) {
        if (position in files.indices) {
            files.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, files.size)
        }
    }
}
