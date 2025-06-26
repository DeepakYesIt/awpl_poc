package com.bussiness.awpl.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemCancelledBinding
import com.bussiness.awpl.model.IncompleteAppoint
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils

class IncompleteAppointAdapter(
    private var appointmentList: List<IncompleteAppoint>,
    private val onRescheduleClick: (IncompleteAppoint) -> Unit
) : RecyclerView.Adapter<IncompleteAppointAdapter.CancelledViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelledViewHolder {

        val binding = ItemCancelledBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CancelledViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CancelledViewHolder, position: Int) {
        val appointment = appointmentList[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int = appointmentList.size

    inner class CancelledViewHolder(private val binding: ItemCancelledBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: IncompleteAppoint) {
            with(binding) {
                Glide.with(binding.root).load(AppConstant.Base_URL+appointment.doctorImage).placeholder(
                    R.drawable.ic_not_found_img).into(doctorImage)
                doctorName.text = appointment.doctorName

                Log.d("TESTING_WORK",appointment.doctorName)
                Log.d("TESTING_WORK",appointment.date)
                dateAndTime.text = appointment.date
                txtTime.text = appointment.time

                rescheduleButton.setOnClickListener {
                    if(appointment.can_reschedule){
                        onRescheduleClick(appointment)
                    }else{
                        LoadingUtils.showErrorDialog(binding.root.context , "You have reached the maximum limit for rescheduling this appointment. You can no longer rebook it.")
                    }

                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(appointmentList: List<IncompleteAppoint>){
        this.appointmentList = appointmentList
        notifyDataSetChanged()
    }
}
