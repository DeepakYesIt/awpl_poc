package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemAppointmentBinding
import com.bussiness.awpl.model.AppointmentModel

class AppointmentAdapter(
    private val appointmentList: MutableList<AppointmentModel>,
    private val onCancelClick: (AppointmentModel) -> Unit,
    private val onRescheduleClick: (AppointmentModel) -> Unit,
    private val onInfoClick: (AppointmentModel, View) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: AppointmentModel) {
            binding.apply {
                doctorName.text = appointment.doctorName
                dateTxt.text = appointment.appointmentDate
                timeTxt.text = appointment.appointmentTime
                doctorImage.setImageResource(appointment.doctorImage)

                cancelButton.setOnClickListener { onCancelClick(appointment) }
                rescheduleButton.setOnClickListener { onRescheduleClick(appointment) }
                infoIcon.setOnClickListener { onInfoClick(appointment, infoIcon) }

                // Hide buttons if cancelled
//                if (appointment.isCancelled) {
//                    cancelButton.visibility = View.GONE
//                    rescheduleButton.visibility = View.GONE
//                } else {
//                    cancelButton.visibility = View.VISIBLE
//                    rescheduleButton.visibility = View.VISIBLE
//                }
            }
        }
    }

    fun removeAppointment(appointment: AppointmentModel) {
        val position = appointmentList.indexOf(appointment)
        if (position != -1) {
            appointmentList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointmentList[position])
    }

    override fun getItemCount(): Int = appointmentList.size
}
