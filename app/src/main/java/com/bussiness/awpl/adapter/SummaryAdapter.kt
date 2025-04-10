package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemSummaryBinding
import com.bussiness.awpl.model.SummaryModel

class SummaryAdapter(private val summaryList: List<SummaryModel>) :
    RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(private val binding: ItemSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(summaryModel: SummaryModel) {
            binding.doctorName.text = summaryModel.name
            binding.doctorExperience.text = summaryModel.experience
            binding.doctorImage.setImageResource(summaryModel.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val binding = ItemSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bind(summaryList[position])
    }

    override fun getItemCount(): Int = summaryList.size
}
