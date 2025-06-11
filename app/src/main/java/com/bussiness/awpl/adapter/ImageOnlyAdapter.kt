package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemImageBinding
import com.bussiness.awpl.model.MediaItem
import com.bumptech.glide.Glide

class ImageOnlyAdapter(
    private val onRemoveClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<ImageOnlyAdapter.ImageViewHolder>() {

    private val imageList = mutableListOf<MediaItem>()

    init {
        setHasStableIds(true)
    }

    fun addImage(mediaItem: MediaItem) {
        imageList.add(mediaItem)
        notifyItemInserted(imageList.lastIndex)
    }

    fun removeImage(mediaItem: MediaItem) {
        val index = imageList.indexOf(mediaItem)
        if (index != -1) {
            imageList.removeAt(index)
            notifyItemRemoved(index)
            onRemoveClick(mediaItem) // Now actually call this here!
        }
    }

    fun getImageList(): List<MediaItem> = imageList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    override fun getItemId(position: Int): Long {
        return imageList[position].uri.hashCode().toLong()
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mediaItem: MediaItem) {
            Glide.with(binding.root.context).load(mediaItem.uri).into(binding.imageView)

            binding.closeButton.setOnClickListener {
                removeImage(mediaItem)
            }
        }
    }
}
