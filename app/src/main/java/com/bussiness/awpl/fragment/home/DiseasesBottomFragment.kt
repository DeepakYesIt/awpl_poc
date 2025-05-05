package com.bussiness.awpl.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.BottomCardDiseaseAdapter
import com.bussiness.awpl.adapter.OrganListAdapter
import com.bussiness.awpl.databinding.FragmentDiseasesBottomBinding
import com.bussiness.awpl.model.OrganDeptModel
import com.bussiness.awpl.model.PrepareData
import com.bussiness.awpl.utils.DiseaseStore
import com.bussiness.awpl.utils.LoadingUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiseasesBottomFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDiseasesBottomBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomCardDiseaseAdapter: OrganListAdapter
    var type:String =""
    private val viewModel: HomeViewModel by viewModels()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        arguments?.let {
            if(it.containsKey("type")){
               type = it.getString("type").toString()
            }
        }
        _binding = FragmentDiseasesBottomBinding.inflate(inflater, container, false)

        Log.d("TESTING_DISEASE","SIZE OF DISEASE IS "+ DiseaseStore.getDiseases().size)
        if(DiseaseStore.getDiseases().size ==0){
            callingDiseaseApi()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickListener()
    }

    private fun setupRecyclerView() {

        bottomCardDiseaseAdapter = OrganListAdapter(DiseaseStore.getDiseases()) { selectedDisease ->
            tempData()
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = bottomCardDiseaseAdapter
        }

    }

    private fun callingDiseaseApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)

            viewModel.getDiseaseList().collect{

                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        it.data?.let { it1 -> bottomCardDiseaseAdapter.updateAdapter(it1) }
                    }
                    is NetworkResult.Error ->{
                    }
                    else ->{

                    }
                }
            }
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
//        val result = if((0..1).random() == 0)"one" else " two"

        if (type == "symptom"){
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
