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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AppointmentAdapter(
    private var appointmentList: MutableList<UpcomingModel>,
    private val onCancelClick: (UpcomingModel) -> Unit,
    private val onRescheduleClick: (UpcomingModel) -> Unit,
    private val onInfoClick: (UpcomingModel, View) -> Unit,
    private val startAppoitmentClick :(UpcomingModel) ->Unit
    ) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: UpcomingModel) {
            binding.apply {
                doctorName.text = appointment.doctorName
                dateTxt.text = appointment.date
                timeTxt.text = appointment.time
              //  doctorImage.setImageResource(appointment.doctorImage)
                if(isCurrentTimeInRange(appointment.time)){
                    twoBtn.visibility=View.GONE
                    startAppointmentBtn.visibility =View.VISIBLE
                }else{
                    twoBtn.visibility =View.VISIBLE
                    startAppointmentBtn.visibility =View.GONE
                }
                Log.d("TESTING_UPCOMING","name :- "+ appointment.doctorName+" Date:- "+ appointment.date+" Time:- "+appointment.time)
                Glide.with(binding.root.context).load(AppConstant.Base_URL+appointment.doctorImage).into(doctorImage)

                cancelButton.setOnClickListener { onCancelClick(appointment) }
                rescheduleButton.setOnClickListener { onRescheduleClick(appointment) }
                infoIcon.setOnClickListener { onInfoClick(appointment, infoIcon) }

                startAppointmentBtn.setOnClickListener { startAppoitmentClick(appointment) }
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


    fun isCurrentTimeInRange(timeRange: String): Boolean {
        try {
            // Clean up and normalize the time range
            val cleaned = timeRange.trim().replace("\\s+".toRegex(), "")
            // Example cleaned: "02:15-02:30PM"

            val amPm = cleaned.takeLast(2) // "AM" or "PM"
            val times = cleaned.removeSuffix(amPm).split("-")
            if (times.size != 2) return false

            val startTimeStr = times[0] + " " + amPm // "02:15 PM"
            val endTimeStr = times[1] + " " + amPm   // "02:30 PM"

            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val now = Calendar.getInstance()
            val currentTime = formatter.format(now.time)

            val current = formatter.parse(currentTime)
            val start = formatter.parse(startTimeStr)
            val end = formatter.parse(endTimeStr)

            return current != null && start != null && end != null &&
                    current >= start && current <= end

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
