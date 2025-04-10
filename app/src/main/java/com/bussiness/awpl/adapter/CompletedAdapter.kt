package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemCompletedBinding
import com.bussiness.awpl.model.AppointmentModel

class CompletedAdapter(
    private val appointments: List<AppointmentModel>,
    private val onCheckDetailsClick: (AppointmentModel) -> Unit,
    private val onDownloadPrescriptionClick: (AppointmentModel) -> Unit
) : RecyclerView.Adapter<CompletedAdapter.CompleteViewHolder>() {

    inner class CompleteViewHolder(private val binding: ItemCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: AppointmentModel) {
            binding.apply {
                consultationTxt.text = appointment.consultationType
                doctorName.text = appointment.doctorName
                dateTxt.text = appointment.appointmentDate
                timeTxt.text = appointment.appointmentTime
                uploadDate.text = appointment.uploadDate
                doctorImage.setImageResource(appointment.doctorImage)
                checkDetailBtn.setOnClickListener { onCheckDetailsClick(appointment) }
                downloadPrescriptionBtn.setOnClickListener { onDownloadPrescriptionClick(appointment) }

                if(appointment.consultationType == "Scheduled Call Consultations"){
                    llAppointmentDetail.visibility = ViewGroup.VISIBLE
                    llDoctorsProfile.visibility = ViewGroup.VISIBLE
                    llUploadDateDetails.visibility = ViewGroup.GONE
                }else{
                    llAppointmentDetail.visibility = ViewGroup.GONE
                    llDoctorsProfile.visibility = ViewGroup.GONE
                    llUploadDateDetails.visibility = ViewGroup.VISIBLE
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
}
