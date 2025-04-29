package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ItemFaqBinding
import com.bussiness.awpl.model.FAQItem

class FAQAdapter(private var faqList: List<FAQItem>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(private val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(faqItem: FAQItem, position: Int) {
            binding.tvQuestion.text = faqItem.question
            binding.tvAnswer.text = faqItem.answer

            // Set visibility based on expanded state
            binding.tvAnswer.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE
            binding.imgExpand.setImageResource(
                if (faqItem.isExpanded) R.drawable.ic_less else R.drawable.ic_plus
            )

            // Expand/Collapse when clicking the icon
            binding.imgExpand.setOnClickListener {
                faqItem.isExpanded = !faqItem.isExpanded
                notifyItemChanged(position) // Update the specific item
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(faqList[position], position)
    }

    override fun getItemCount(): Int = faqList.size

    fun updateAdapter(faqList: List<FAQItem>){
        this.faqList = faqList
        notifyDataSetChanged()
    }
}
