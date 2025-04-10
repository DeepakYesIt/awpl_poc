package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemSelectedFileBinding
import com.bussiness.awpl.model.SelectedFile

class SelectedMediaAdapter(
    private val fileList: List<SelectedFile>
) : RecyclerView.Adapter<SelectedMediaAdapter.FileViewHolder>() {

    inner class FileViewHolder(val binding: ItemSelectedFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemSelectedFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = fileList[position]


        if (file.type == "image") {
            holder.binding.imageText.text = file.name
            Glide.with(holder.itemView.context)
                .load(file.uri)
                .into(holder.binding.imageView)
        } else if (file.type == "video") {
            holder.binding.imageView.setImageResource(R.drawable.video_svg)
            holder.binding.imageText.text = file.name
        }else{
            holder.binding.imageView.setImageResource(R.drawable.pdf_ic)
            holder.binding.imageText.text = file.name
        }
    }

    override fun getItemCount(): Int = fileList.size
}
