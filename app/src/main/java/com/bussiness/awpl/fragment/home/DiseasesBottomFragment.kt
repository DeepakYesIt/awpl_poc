package com.bussiness.awpl.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bussiness.awpl.HealthDataStore
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.OrganListAdapter
import com.bussiness.awpl.databinding.FragmentDiseasesBottomBinding
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.DiseaseStore
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.viewmodel.DiseaseModel
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
    private  var diseaseList: MutableList<DiseaseModel> = mutableListOf()
//    private val diseasesList = listOf(
//        OrganDeptModel(R.drawable.rectangle, "Kidney Disease"),
//        OrganDeptModel(R.drawable.rectangle, "Liver Disease"),
//        OrganDeptModel(R.drawable.rectangle, "Heart Disease"),
//        OrganDeptModel(R.drawable.rectangle, "Blood Pressure"),
//        OrganDeptModel(R.drawable.rectangle, "Diabetes"),
//        OrganDeptModel(R.drawable.rectangle, "Tuberculosis(TB)"),
//        OrganDeptModel(R.drawable.rectangle, "Typhoid"),
//        OrganDeptModel(R.drawable.rectangle, "Rheumatoid Arthritis"),
//        OrganDeptModel(R.drawable.rectangle, "Gynic Disease"),
//        OrganDeptModel(R.drawable.rectangle, "Allergy"),
//        OrganDeptModel(R.drawable.rectangle, "Thyroid"),
//        OrganDeptModel(R.drawable.rectangle, "Gastric"),
//        OrganDeptModel(R.drawable.rectangle, "Skin Disease"),
//        OrganDeptModel(R.drawable.rectangle, "Acidity"),
//        OrganDeptModel(R.drawable.rectangle, "Piles"),
//        OrganDeptModel(R.drawable.rectangle, "Constipation"),
//        OrganDeptModel(R.drawable.rectangle, "Cholesterol"),
//        OrganDeptModel(R.drawable.rectangle, "Stroke"),
//        OrganDeptModel(R.drawable.rectangle, "Thalassemia"),
//        OrganDeptModel(R.drawable.rectangle, "Cancer"),
//        OrganDeptModel(R.drawable.rectangle, "Asthma"),
//    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        diseaseList = HealthDataStore.getHealthNeeds().toMutableList()

        arguments?.let {
            if(it.containsKey("type")){
               type = it.getString("type").toString()
            }
//            if(it.containsKey(AppConstant.DISEASE_LIST)){
//                diseaseList = (arguments?.getSerializable(AppConstant.DISEASE_LIST) as? ArrayList<DiseaseModel>)!!
//                Log.d("TESTING_LIST_SIZE",diseaseList.size.toString())
//            }
        }

        _binding = FragmentDiseasesBottomBinding.inflate(inflater, container, false)

        Log.d("TESTING_DISEASE","SIZE OF DISEASE IS "+ DiseaseStore.getDiseases().size)
        if(DiseaseStore.getDiseases().size ==0){
          //  callingDiseaseApi()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickListener()
    }

    private fun setupRecyclerView() {

        bottomCardDiseaseAdapter = OrganListAdapter(diseaseList) { selectedDisease ->

            tempData(selectedDisease)
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

    private fun tempData(selectedDisease: DiseaseModel) {
//        val result = if((0..1).random() == 0)"one" else " two"
        var bundle = Bundle().apply {
            putInt(AppConstant.DISEASE_ID, selectedDisease.id)
            putString(AppConstant.TYPE,type)
        }

        if (selectedDisease.category == "major"){
            findNavController().navigate( R.id.doctorConsultationFragment,bundle)
        }
        else{
             findNavController().navigate( R.id.onlineConsultationFragment,bundle)
         }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
