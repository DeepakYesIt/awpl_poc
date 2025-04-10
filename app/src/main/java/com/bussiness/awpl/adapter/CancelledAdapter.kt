package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemCancelledBinding
import com.bussiness.awpl.model.AppointmentModel

class CancelledAdapter(
    private val appointmentList: List<AppointmentModel>,
    private val onRescheduleClick: (AppointmentModel) -> Unit
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

    inner class CancelledViewHolder(private val binding: ItemCancelledBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: AppointmentModel) {
            with(binding) {
                doctorImage.setImageResource(appointment.doctorImage)
                doctorName.text = appointment.doctorName
                dateAndTime.text = appointment.appointmentDate

                rescheduleButton.setOnClickListener {
                    onRescheduleClick(appointment)
                }
            }
        }
    }
}
