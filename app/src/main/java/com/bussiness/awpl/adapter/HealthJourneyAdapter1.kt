package com.bussiness.awpl.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemScheduleCallBinding
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.HealthListModel

class HealthJourneyAdapter1(private val items: List<HealthListModel>, private val onScheduleCallClick: (HealthListModel) -> Unit) :
    RecyclerView.Adapter<HealthJourneyAdapter1.HealthJourneyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthJourneyViewHolder {
        val binding = ItemScheduleCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HealthJourneyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HealthJourneyViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onScheduleCallClick)

        if(position ==0){

            Log.d("TESTING_ANDROID"," I AM INSIDE POSITION 1")

            holder.binding.imgSh.setImageResource(R.drawable.ic_event_card_1)

        }
        else if(position ==1){

            Log.d("TESTING_ANDROID"," I AM INSIDE POSITION 2")

            holder.binding.imgSh.visibility= View.GONE
            holder.binding.rl1.visibility = View.VISIBLE
            holder.binding.imgSh1.setImageResource(R.drawable.ic_event_card_2)

        }
        else if(position ==2){

            Log.d("TESTING_ANDROID"," I AM INSIDE POSITION 3")
            holder.binding.imgSh.setImageResource(R.drawable.ic_event_card_3)

        }

        // Alternate background colors
        if (position % 2 == 0) {
            //  holder.binding.root.setBackgroundResource(R.drawable.card_linear_bg)
            // holder.binding.headingTxt.setTextColor(holder.binding.root.context.getColor(R.color.white)) // Default color
        } else {
            // holder.binding.root.setBackgroundResource(R.drawable.banner_ic_bg)
            //  holder.binding.headingTxt.setTextColor(holder.binding.root.context.getColor(R.color.blue)) // Use a defined color
        }

    }

    override fun getItemCount(): Int = items.size

    inner class HealthJourneyViewHolder(val binding: ItemScheduleCallBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HealthListModel, onScheduleCallClick: (HealthListModel) -> Unit) {
            // binding.headingTxt.text = item.title
            // binding.doctorImage.setImageResource(item.imageRes)
            // binding.scheduleCallBtn.setOnClickListener { onScheduleCallClick(item) }
        }
    }


}