package com.bussiness.awpl.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemBrowseVideoBinding
import com.bussiness.awpl.databinding.ItemVideoGalleryBinding
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.VideoModel
import com.bussiness.awpl.utils.AppConstant

class VideoGalleryAdapter(
    private var items: List<VideoModel>,
    private val onVideoClick: (VideoModel) -> Unit
) : RecyclerView.Adapter<VideoGalleryAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(items[position], onVideoClick)
    }

    override fun getItemCount(): Int = items.size

    inner class VideoViewHolder(private val binding: ItemVideoGalleryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoModel, onVideoClick: (VideoModel) -> Unit) {

            Log.d("TESTING_URL",AppConstant.Base_URL+item.thumbnail_path)
            Glide.with(binding.root.context).load(AppConstant.Base_URL+item.thumbnail_path).placeholder(
                R.drawable.lady_dc).into(binding.imgThumbnail)

            binding.tvTitle.text = item.title

            binding.root.setOnClickListener {
                onVideoClick(item)
            }

        }


    }

    fun updateAdapter( items: List<VideoModel>){
        this.items = items;
        Log.d("TESTING_URL","HERE IN THE UPDATE ADAPTER")
        notifyDataSetChanged()
    }

}
