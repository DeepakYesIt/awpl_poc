package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R

class StateAdapter(
    private val stateList: List<String>,
    private val onStateSelected: (String) -> Unit
) : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    inner class StateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stateName: TextView = itemView.findViewById(R.id.stateName)
        fun bind(state: String) {
            stateName.text = state
            itemView.setOnClickListener {
                onStateSelected(state)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_state, parent, false)
        return StateViewHolder(view)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        holder.bind(stateList[position])
    }

    override fun getItemCount(): Int = stateList.size
}
