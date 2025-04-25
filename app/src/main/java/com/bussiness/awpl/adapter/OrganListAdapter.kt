package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemOrgansBinding
import com.bussiness.awpl.model.OrganDeptModel

class OrganListAdapter(private val items: List<OrganDeptModel>,  private val onItemClick: (OrganDeptModel) -> Unit) :
    RecyclerView.Adapter<OrganListAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: ItemOrgansBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrganDeptModel) {
            binding.organDeptImg.setImageResource(item.imageResId)
            binding.deptName.text = item.title
            binding.root1.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemOrgansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
