package com.bussiness.awpl.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemCancelledBinding
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.utils.AppConstant

class CancelledAdapter(
    private var appointmentList: List<CancelledAppointment>,
    private val onRescheduleClick: (CancelledAppointment) -> Unit
) : RecyclerView.Adapter<CancelledAdapter.CancelledViewHolder>() {

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

        fun bind(appointment: CancelledAppointment) {
            with(binding) {
                Glide.with(binding.root).load(AppConstant.Base_URL+appointment.doctorImage).placeholder(
                    R.drawable.ic_not_found_img).into(doctorImage)
                doctorName.text = appointment.doctorName
                rescheduleButton.visibility = View.GONE
                Log.d("TESTING_WORK",appointment.doctorName)
                Log.d("TESTING_WORK",appointment.date)
                dateAndTime.text = appointment.date
                txtTime.text = appointment.time
                if(appointment.is_free_call){

                    tagFreeText.visibility =View.VISIBLE
                }
                else{
                    tagFreeText.visibility =View.GONE
                }
                rescheduleButton.setOnClickListener {
                    onRescheduleClick(appointment)
                }
             }
           }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(appointmentList: List<CancelledAppointment>){
        this.appointmentList = appointmentList
        notifyDataSetChanged()
    }
}
