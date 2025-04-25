package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemCompletedBinding
import com.bussiness.awpl.model.AppointmentModel

class CompletedAdapter(
    private var appointments: List<AppointmentModel>,
    private val onCheckDetailsClick: (AppointmentModel) -> Unit, private val onDownloadPrescriptionClick: (AppointmentModel) -> Unit
) : RecyclerView.Adapter<CompletedAdapter.CompleteViewHolder>() {

    var type :Boolean = true

    inner class CompleteViewHolder(private val binding: ItemCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: AppointmentModel) {
            binding.apply {
                if(type) {
                    llAppointment.visibility = View.VISIBLE
                    llSymptom.visibility =View.GONE

                //                    consultationTxt.text = appointment.consultationType
                //                    doctorName.text = appointment.doctorName
//                                    dateTxt.text = appointment.appointmentDate
//                                    timeTxt.text = appointment.appointmentTime
//                                    uploadDate.text = appointment.uploadDate
//                                    doctorImage.setImageResource(appointment.doctorImage)
//                                    checkDetail11.setOnClickListener { onCheckDetailsClick(appointment) }
//                                    downloadPrescriptionBtn1.setOnClickListener {
//                                     onDownloadPrescriptionClick(
//                            appointment
//                        )
//                    }


              //      if (appointment.consultationType == "Scheduled Call Consultations") {
//
//                    } else {
//                        llAppointmentDetail.visibility = ViewGroup.GONE
//                        llDoctorsProfile.visibility = ViewGroup.GONE
//                        llUploadDateDetails.visibility = ViewGroup.VISIBLE
//                    }

                }
                else{
                    llAppointment.visibility = View.GONE
                    llSymptom.visibility =View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteViewHolder {
        val binding =
            ItemCompletedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompleteViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun update(value:Boolean, appointments: List<AppointmentModel>){
        this.type = value
        this.appointments = appointments
        notifyDataSetChanged()
    }

}
