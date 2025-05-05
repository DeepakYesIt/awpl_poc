package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bussiness.awpl.databinding.ItemOrgansBinding
import com.bussiness.awpl.model.OrganDeptModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.DiseaseModel

class OrganListAdapter(private var items: List<DiseaseModel>, private val onItemClick: (DiseaseModel) -> Unit) :
    RecyclerView.Adapter<OrganListAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: ItemOrgansBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiseaseModel) {


            Glide.with(binding.root.context).load(AppConstant.Base_URL+MultipartUtil.ensureStartsWithSlash(item.icon_path)).into(binding.organDeptImg)

            binding.deptName.text = item.name
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

    fun updateAdapter(items: List<DiseaseModel>){
        this.items = items
        notifyDataSetChanged()
    }

}
