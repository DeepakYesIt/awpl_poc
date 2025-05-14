package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R

class TimeSlotAdapter(
    private var timeSlots: List<String>,
    private val onTimeSlotSelected: (String) -> Unit
    ) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTimeSlot: TextView = itemView.findViewById(R.id.textTimeSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.textTimeSlot.text = timeSlot

        // Apply selection state
        holder.textTimeSlot.setBackgroundResource(
            if (holder.adapterPosition == selectedPosition) R.drawable.bg_selected_time_slot
            else R.drawable.bg_unselected_time_slot
        )
        holder.textTimeSlot.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (holder.adapterPosition == selectedPosition) {
                    R.color.white // selected text color
                } else {
                    R.color.greyColor // unselected text color
                }
            )
        )

        holder.itemView.setOnClickListener {
            val newPosition = holder.adapterPosition
            if (newPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousSelected = selectedPosition
            selectedPosition = newPosition

            notifyItemChanged(previousSelected) // Update old selection
            notifyItemChanged(selectedPosition) // Update new selection

            onTimeSlotSelected(timeSlot)
        }
    }

    override fun getItemCount(): Int = timeSlots.size

    fun updateAdapter( timeSlots: List<String>){
        this.timeSlots = timeSlots
        notifyDataSetChanged()
    }


    }
