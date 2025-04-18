package com.bussiness.awpl.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.BottomCardDiseaseAdapter
import com.bussiness.awpl.databinding.FragmentDiseasesBottomBinding
import com.bussiness.awpl.model.OrganDeptModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DiseasesBottomFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDiseasesBottomBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomCardDiseaseAdapter: BottomCardDiseaseAdapter

    private val diseasesList = listOf(
        OrganDeptModel(R.drawable.rectangle, "Kidney Disease"),
        OrganDeptModel(R.drawable.rectangle, "Liver Disease"),
        OrganDeptModel(R.drawable.rectangle, "Heart Disease"),
        OrganDeptModel(R.drawable.rectangle, "Blood Pressure"),
        OrganDeptModel(R.drawable.rectangle, "Diabetes"),
        OrganDeptModel(R.drawable.rectangle, "Tuberculosis(TB)"),
        OrganDeptModel(R.drawable.rectangle, "Typhoid"),
        OrganDeptModel(R.drawable.rectangle, "Rheumatoid Arthritis"),
        OrganDeptModel(R.drawable.rectangle, "Gynic Disease"),
        OrganDeptModel(R.drawable.rectangle, "Allergy"),
        OrganDeptModel(R.drawable.rectangle, "Thyroid"),
        OrganDeptModel(R.drawable.rectangle, "Gastric"),
        OrganDeptModel(R.drawable.rectangle, "Skin Disease"),
        OrganDeptModel(R.drawable.rectangle, "Acidity"),
        OrganDeptModel(R.drawable.rectangle, "Piles"),
        OrganDeptModel(R.drawable.rectangle, "Constipation"),
        OrganDeptModel(R.drawable.rectangle, "Cholesterol"),
        OrganDeptModel(R.drawable.rectangle, "Stroke"),
        OrganDeptModel(R.drawable.rectangle, "Thalassemia"),
        OrganDeptModel(R.drawable.rectangle, "Cancer"),
        OrganDeptModel(R.drawable.rectangle, "Asthma"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiseasesBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickListener()
    }
    private fun setupRecyclerView() {
        bottomCardDiseaseAdapter = BottomCardDiseaseAdapter(diseasesList) { selectedDisease ->
            Toast.makeText(requireContext(), "Selected Disease: ${selectedDisease.title}", Toast.LENGTH_SHORT).show()
            tempData()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bottomCardDiseaseAdapter
        }
    }

    private fun clickListener() {
        binding.apply {
            crossBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun tempData(){
        val result = if((0..1).random() == 0)"one" else " two"
        if (result == "one"){
            findNavController().navigate(R.id.onlineConsultationFragment)
        }else{
            findNavController().navigate(R.id.doctorConsultationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
