package com.bussiness.awpl.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemYourDoctorBinding
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.SummaryModel
import com.bussiness.awpl.utils.AppConstant

class YourDoctorAdapter(private var summaryList: MutableList<DoctorModel>) :
    RecyclerView.Adapter<YourDoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(private val binding: ItemYourDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(summaryModel: DoctorModel) {
            binding.doctorName.text = summaryModel.doctorName
            binding.doctorExperience.text = "Experience: ${summaryModel.experience_yrs}"
            summaryModel.doctorImage?.let {
                Glide.with(binding.root.context)
                    .load(AppConstant.Base_URL + summaryModel.doctorImage).placeholder(
                    R.drawable.lady_dc
                ).into(binding.doctorImage)
            }

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


    fun updateAdapter(summaryList: MutableList<DoctorModel>){
        this.summaryList = summaryList
        Log.d("TESTING_DOCTOR","Inside the summary list "+summaryList.size.toString())
        notifyDataSetChanged()
    }
}
