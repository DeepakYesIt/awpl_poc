package com.bussiness.awpl.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemCancelledBinding
import com.bussiness.awpl.databinding.UploadSymptomsAdapterBinding
import com.bussiness.awpl.model.AppointmentModel
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.model.CompletedSymptomsModel
import com.bussiness.awpl.utils.AppConstant

class SymptomsUploadCompleteAdapter(private var appointments: List<CompletedSymptomsModel>,
                                    private val onDownloadPrescriptionClick: (CompletedSymptomsModel) -> Unit) :
    RecyclerView.Adapter<SymptomsUploadCompleteAdapter.CancelledViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelledViewHolder {

        val binding = UploadSymptomsAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CancelledViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CancelledViewHolder, position: Int) {
        val appointment = appointments[position]
        Log.d("INSIDE_WORKING_SIZE",appointment.upload_date.toString() +position + appointments.size)
        holder.bind(appointment)

    }

    override fun getItemCount(): Int = appointments.size

    inner class CancelledViewHolder(private val binding: UploadSymptomsAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(appointment: CompletedSymptomsModel) {
               with(binding) {
                Log.d("TESTING_UPLOAD_WORK",appointment.upload_date.toString())
                tvUploadDate.text = appointment.upload_date
                if(appointment.file_path != null){
                rescheduleButton.setOnClickListener {
                    onDownloadPrescriptionClick(appointment)
                  }
                }
                else{
                    rescheduleButton.setBackgroundResource(R.drawable.dark_grey_btn)
                }
            }
        }
    }

    fun updateAdapter(appointmentList: List<CompletedSymptomsModel>){
        this.appointments = appointmentList
        Log.d("TESTING_APPOITMENT_SIZE",this.appointments.size.toString())
        notifyDataSetChanged()
    }
}