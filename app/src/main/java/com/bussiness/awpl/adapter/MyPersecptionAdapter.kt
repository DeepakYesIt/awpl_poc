package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemFaqBinding
import com.bussiness.awpl.databinding.ItemUpcomingLayoutBinding
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.Prescription

class MyPersecptionAdapter(private var perceptionList: MutableList<Prescription>,private val onScheduleCallClick: (Prescription) -> Unit) : RecyclerView.Adapter<MyPersecptionAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(private val binding: ItemUpcomingLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(presItem: Prescription, position: Int) {
           binding.root5.setOnClickListener {
            onScheduleCallClick(presItem)
           }
//            binding.imgBack.setOnClickListener {
//                onScheduleCallClick(presItem)
//            }
            if(presItem.referred != null){
             binding.tvRefer.setText(presItem.referred)
            }else{
                binding.tvRefer.visibility= View.GONE
            }
            binding.tvTime.text = presItem.time
            binding.tvDate.text = presItem.date
            binding.tvDiagonosis.text = presItem.diagnosis
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemUpcomingLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(perceptionList[position], position)

    }

    override fun getItemCount(): Int = perceptionList.size

    fun updateAdapter(perceptionList: MutableList<Prescription>){
        this.perceptionList = perceptionList
        notifyDataSetChanged()
    }
}