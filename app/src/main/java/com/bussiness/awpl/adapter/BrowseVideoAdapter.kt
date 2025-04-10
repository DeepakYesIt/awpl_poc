package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemBrowseVideoBinding
import com.bussiness.awpl.model.HealthJourneyItem

class BrowseVideoAdapter(
    private val items: List<HealthJourneyItem>,
    private val onVideoClick: (HealthJourneyItem) -> Unit
) : RecyclerView.Adapter<BrowseVideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemBrowseVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(items[position], onVideoClick)
    }

    override fun getItemCount(): Int = items.size

    inner class VideoViewHolder(private val binding: ItemBrowseVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HealthJourneyItem, onVideoClick: (HealthJourneyItem) -> Unit) {
            binding.videoThumbnail.setImageResource(item.imageRes)
            binding.videoTxt.text = item.title

            binding.root.setOnClickListener {
                onVideoClick(item)
            }
        }
    }
}
