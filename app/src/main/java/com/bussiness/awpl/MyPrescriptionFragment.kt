package com.bussiness.awpl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.adapter.MyPersecptionAdapter
import com.bussiness.awpl.databinding.FragmentMyPrescriptionBinding
import com.bussiness.awpl.model.PrepareData
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.MyPrescriptionViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyPrescriptionFragment : Fragment() {

    lateinit var binding :FragmentMyPrescriptionBinding
    private var isSelected = false
    lateinit var adapter :MyPersecptionAdapter
    var whichselected = "all";

    lateinit var viewModel :MyPrescriptionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MyPrescriptionViewModel::class.java]
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPrescriptionBinding.inflate(LayoutInflater.from(requireContext()))
        adapter = MyPersecptionAdapter(mutableListOf()){ pres->
            var chatId = if(pres.chat_id != null) pres.chat_id else null
            var bundle =Bundle().apply {
                putInt(AppConstant.AppoitmentId,pres.prescription_id)
                putString(AppConstant.Chat, chatId)
            }
            if(chatId != null) {
                findNavController().navigate(R.id.doctorChatFragment, bundle)
            }
        }
        binding.recyclerPresecption.adapter = adapter
        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.llFilters.setOnClickListener {
            isSelected = !isSelected
            if (isSelected) {
                    binding.imgIconUpDown.setImageResource(R.drawable.up_arrow)
                    filterPopUp(it)
            }
            else {
                    binding.imgIconUpDown.setImageResource(R.drawable.ic_down_white)
            }
        }
        callingMyPrescriptionApi("all")
        return binding.root
    }

    private fun callingMyPrescriptionApi(type:String){
        lifecycleScope.launch {
            try {
                LoadingUtils.showDialog(requireContext(),false)
                viewModel.myPrescription(type).collect{

                    when(it){
                        is NetworkResult.Success ->{
                            LoadingUtils.hideDialog()
                            var data = it.data
                            if (data != null) {
                                adapter.updateAdapter(data)
                            }
                        }
                        is NetworkResult.Error ->{
                            LoadingUtils.hideDialog()

                        }
                        else ->{

                        }
                    }


                }
            }catch(e:Exception){

            }
        }

    }




    private fun filterPopUp(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_filter_my_prescription, null)

        val popupWindow = PopupWindow(
            popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )

         var myPresc = popupView.findViewById<MaterialCheckBox>(R.id.appCompatCheckBox)
         var otherPresc = popupView.findViewById<MaterialCheckBox>(R.id.appCompatCheckBox2)

        if(whichselected == "me"){
            myPresc.isChecked = true
        }
        else if(whichselected == "others"){
            otherPresc.isChecked = true
        }

        myPresc.setOnClickListener {
            if(myPresc.isChecked){
                otherPresc.isChecked = false
                whichselected ="me"
                callingMyPrescriptionApi("me")
                popupWindow.dismiss()
            }else{
                whichselected ="all"
                callingMyPrescriptionApi("all")
                popupWindow.dismiss()
            }
        }

        otherPresc.setOnClickListener {
            if(otherPresc.isChecked) {
                otherPresc.isChecked = false
                whichselected ="others"
                callingMyPrescriptionApi("others")
                popupWindow.dismiss()
            }else{
                whichselected="all"
                callingMyPrescriptionApi("all")
                popupWindow.dismiss()
            }
        }


//        if(whichselected ==2){
//            otherPresc.isChecked = true
//            myPresc.isChecked = false
//        }else{
//            otherPresc.isChecked = false
//            myPresc.isChecked = true
//        }
//
//        myPresc.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//              otherPresc.isChecked = false
//                whichselected = 1
//             // adapter.updateAdapter(PrepareData.getSelfReferredPrescriptions(PrepareData.getDummyPrescriptions()))
//                popupWindow.dismiss()
//            }
//            else {
//                whichselected = 2
//                otherPresc.isChecked = true
//              //  adapter.updateAdapter(PrepareData.filterPrescriptionsByReferred(PrepareData.getDummyPrescriptions()))
//                popupWindow.dismiss()
//            }
//        }
//
//        otherPresc.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                whichselected = 2
//                myPresc.isChecked = false
//
//              //  adapter.updateAdapter(PrepareData.filterPrescriptionsByReferred(PrepareData.getDummyPrescriptions()))
//                popupWindow.dismiss()
//            }
//            else {
//                whichselected = 1
//                myPresc.isChecked = true
//              //  adapter.updateAdapter(PrepareData.getSelfReferredPrescriptions(PrepareData.getDummyPrescriptions()))
//                popupWindow.dismiss()
//
//            }
//        }


        // Handle dismiss listener to reset UI
        popupWindow.setOnDismissListener {
            isSelected = false
            binding.imgIconUpDown.setImageResource(R.drawable.ic_down_white)
        }

        // Show popup below the clicked view
        popupWindow.showAsDropDown(anchorView, 0, 10)

        popupView.setOnClickListener {
            popupWindow.dismiss()
        }


    }





}