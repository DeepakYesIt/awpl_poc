package com.bussiness.awpl.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemSummaryBinding
import com.bussiness.awpl.model.Doctor
import com.bussiness.awpl.model.SummaryModel
import com.bussiness.awpl.utils.AppConstant

class SummaryAdapter(private var summaryList: List<Doctor>) :
    RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(private val binding: ItemSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(summaryModel: Doctor) {
            binding.doctorName.text = summaryModel.name
            binding.doctorExperience.text = "Experience : ${summaryModel.experience_yrs}"
            Log.d("TESTING_PAYMENT",AppConstant.Base_URL+ summaryModel.profile_path)
            Glide.with(binding.root.context)
                .load(AppConstant.Base_URL+summaryModel.profile_path ).placeholder(
                R.drawable.ic_not_found_img
            ).into(binding.doctorImage)
            //binding.doctorImage.setImageResource(summaryModel.imageResId)
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

    fun updateAdapter( summaryList: List<Doctor>){
       this.summaryList = summaryList
        notifyDataSetChanged()
    }


    }
