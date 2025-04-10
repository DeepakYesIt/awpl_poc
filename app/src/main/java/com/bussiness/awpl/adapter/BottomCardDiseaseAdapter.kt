package com.bussiness.awpl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.ItemDiseaseBinding
import com.bussiness.awpl.model.OrganDeptModel

class BottomCardDiseaseAdapter(
    private val diseaseList: List<OrganDeptModel>,
    private val onItemClick: (OrganDeptModel) -> Unit
) : RecyclerView.Adapter<BottomCardDiseaseAdapter.DiseaseViewHolder>() {

    inner class DiseaseViewHolder(private val binding: ItemDiseaseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(disease: OrganDeptModel) {
            binding.diseaseImage.setImageResource(disease.imageResId)
            binding.diseaseName.text = disease.title

            binding.root.setOnClickListener {
                onItemClick(disease)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        holder.bind(diseaseList[position])
    }

    override fun getItemCount(): Int = diseaseList.size
}
