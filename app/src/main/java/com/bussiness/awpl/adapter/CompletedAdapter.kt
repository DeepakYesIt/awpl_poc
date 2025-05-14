package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.databinding.ItemCompletedBinding
import com.bussiness.awpl.model.AppointmentModel
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.utils.AppConstant

class CompletedAdapter(
    private var appointments: List<CompletedAppointmentModel>,
    private val onCheckDetailsClick: (AppointmentModel) -> Unit,
    private val onDownloadPrescriptionClick: (AppointmentModel) -> Unit
) : RecyclerView.Adapter<CompletedAdapter.CompleteViewHolder>() {

    var type :Boolean = true

    inner class CompleteViewHolder(private val binding: ItemCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(appointment: CompletedAppointmentModel) {
               binding.apply {
                     llAppointment.visibility = View.VISIBLE
                     llSymptom.visibility =View.GONE
                    var obj = appointments.get(position)
                     dateTxt.setText(obj.date)
                     timeTxt.text = obj.time
                     doctorName.text = obj.doctorName
                     Glide.with(binding.root.context).load(AppConstant.Base_URL+obj.doctorImage).into(binding.doctorImage)
               }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteViewHolder {
        val binding = ItemCompletedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompleteViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun update(value:Boolean, appointments: List<CompletedAppointmentModel>){
        this.type = value
        this.appointments = appointments
        notifyDataSetChanged()
    }

    fun updateAdapter(appointments :List<CompletedAppointmentModel>){
        this.appointments = appointments
        notifyDataSetChanged()
    }

}
