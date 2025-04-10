package com.bussiness.awpl.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemBrowseVideoBinding
import com.bussiness.awpl.databinding.ItemVideoGalleryBinding
import com.bussiness.awpl.model.HealthJourneyItem

class VideoGalleryAdapter(
    private val items: List<HealthJourneyItem>,
    private val onVideoClick: (HealthJourneyItem) -> Unit
) : RecyclerView.Adapter<VideoGalleryAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(items[position], onVideoClick)
    }

    override fun getItemCount(): Int = items.size

    inner class VideoViewHolder(private val binding: ItemVideoGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HealthJourneyItem, onVideoClick: (HealthJourneyItem) -> Unit) {
            binding.imgThumbnail.setImageResource(item.imageRes)
            binding.tvTitle.text = item.title

            binding.root.setOnClickListener {
                onVideoClick(item)
            }
        }
    }
}
