package com.bussiness.awpl.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemFaqBinding
import com.bussiness.awpl.databinding.ItemUpcomingLayoutBinding
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.Prescription
import com.bussiness.awpl.model.PrescriptionModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.DownloadWorker

class MyPersecptionAdapter(private var perceptionList: MutableList<PrescriptionModel>,private val onScheduleCallClick: (PrescriptionModel) -> Unit) : RecyclerView.Adapter<MyPersecptionAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(private val binding: ItemUpcomingLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(presItem: PrescriptionModel, position: Int) {
           binding.root5.setOnClickListener {
            onScheduleCallClick(presItem)
           }
//            binding.imgBack.setOnClickListener {
//                onScheduleCallClick(presItem)
//            }
            if(presItem.referred_name != null){
             binding.tvRefer.setText(presItem.referred_name)
            }else{
                binding.tvRefer.visibility= View.GONE
            }
            binding.tvTime.text = presItem.time
            binding.tvDate.text = presItem.date
            binding.tvDiagonosis.text = presItem.diagnosis

            binding.llDwnld.setOnClickListener {
                if(perceptionList.get(position).file_path != null) {
                    DownloadWorker().
                    downloadPdfWithNotification(binding.root.context,AppConstant.Base_URL+perceptionList.get(position).file_path,"Prescription_"+"file_${(10000..99999).random()}.pdf")
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemUpcomingLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(perceptionList[position], position)

    }

    override fun getItemCount(): Int = perceptionList.size

    fun updateAdapter(perceptionList: MutableList<PrescriptionModel>){
        this.perceptionList = perceptionList
        notifyDataSetChanged()
    }
}