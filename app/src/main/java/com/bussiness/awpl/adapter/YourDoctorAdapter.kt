package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemYourDoctorBinding
import com.bussiness.awpl.model.SummaryModel

class YourDoctorAdapter(private val summaryList: List<SummaryModel>) :
    RecyclerView.Adapter<YourDoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(private val binding: ItemYourDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(summaryModel: SummaryModel) {
            binding.doctorName.text = summaryModel.name
            binding.doctorExperience.text = summaryModel.experience
            binding.doctorImage.setImageResource(summaryModel.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemYourDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(summaryList[position])
    }

    override fun getItemCount(): Int = summaryList.size
}
