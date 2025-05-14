package com.bussiness.awpl.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.databinding.ItemAppointmentBinding
import com.bussiness.awpl.model.AppointmentModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.utils.AppConstant

class AppointmentAdapter(
    private var appointmentList: MutableList<UpcomingModel>,
    private val onCancelClick: (UpcomingModel) -> Unit,
    private val onRescheduleClick: (UpcomingModel) -> Unit,
    private val onInfoClick: (UpcomingModel, View) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: UpcomingModel) {
            binding.apply {
                doctorName.text = appointment.doctorName
                dateTxt.text = appointment.date
                timeTxt.text = appointment.time
              //  doctorImage.setImageResource(appointment.doctorImage)
                Log.d("TESTING_UPCOMING","name :- "+ appointment.doctorName+" Date:- "+ appointment.date+" Time:- "+appointment.time)
                Glide.with(binding.root.context).load(AppConstant.Base_URL+appointment.doctorImage).into(doctorImage)

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

    fun removeAppointment(appointment: UpcomingModel) {
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

     fun updateAdapter(appointmentList: MutableList<UpcomingModel>){
         this.appointmentList =appointmentList
         notifyDataSetChanged()
     }

}
