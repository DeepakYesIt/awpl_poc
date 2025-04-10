package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.databinding.ItemMediaBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType

class MediaAdapter(
    private val mediaList: List<MediaItem>,
    private val onRemove: (MediaItem) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = mediaList[position]
        with(holder.binding) {
            imageView.visibility = View.GONE
            pdfIcon.visibility = View.GONE
            videoPlayIcon.visibility = View.GONE

            when (item.type) {
                MediaType.IMAGE -> {
                    imageView.visibility = View.VISIBLE
                    Glide.with(root.context).load(item.uri).into(imageView)
                }
                MediaType.VIDEO -> {
                    imageView.visibility = View.VISIBLE
                    videoPlayIcon.visibility = View.VISIBLE
                    Glide.with(root.context).load(item.uri).into(imageView)
                }
                MediaType.PDF -> {
                    pdfIcon.visibility = View.VISIBLE
                }
            }

            closeButton.setOnClickListener { onRemove(item) }
        }
    }

    override fun getItemCount() = mediaList.size
}
