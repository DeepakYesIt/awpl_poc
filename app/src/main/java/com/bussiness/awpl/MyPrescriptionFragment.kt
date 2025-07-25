package com.bussiness.awpl

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.activities.PdfActivity
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
//        adapter = MyPersecptionAdapter(mutableListOf()){ pres->
//            var chatId = if(pres.chat_id != null) pres.chat_id else null
//            var bundle =Bundle().apply {
//                putInt(AppConstant.AppoitmentId,pres.prescription_id)
//                putString(AppConstant.Chat, chatId)
//            }
//            if(chatId != null) {
//                findNavController().navigate(R.id.doctorChatFragment, bundle)
//            }
//        }

         adapter = MyPersecptionAdapter(
            mutableListOf(),
            onScheduleCallClick = { pres ->
                val chatId = pres.chat_id
                val bundle = Bundle().apply {
                    putInt(AppConstant.AppoitmentId, pres.appointment_id)
                    putString(AppConstant.Chat, chatId)
                }
                if (chatId != null) {
                    findNavController().navigate(R.id.doctorChatFragment, bundle)
                }
            },
            onViewPdf = { pdfLink ->
                //Log.d("TESTING_URL",AppConstant.Base_URL+pdfLink.toString())

                if(pdfLink != null && pdfLink.isNotEmpty()){
                    var intent = Intent(requireContext(),PdfActivity::class.java)
                    Log.d("TESTING_PDF",pdfLink)
                    intent.putExtra(AppConstant.PDF,pdfLink)
                    startActivity(intent)
                }

//                try {
//                    if (pdfLink.isBlank()) {
//                        Toast.makeText(context, "Invalid PDF link", Toast.LENGTH_SHORT).show()
//                        return@MyPersecptionAdapter
//                    }
//
//                    val uri = Uri.parse(pdfLink)
//
//                    // Check if it's a web link
//                    if (uri.scheme == "http" || uri.scheme == "https") {
//                        // Always open in browser for remote PDFs
//                        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
//                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        context?.startActivity(browserIntent)
//                    } else {
//                        // Try to open local PDF with viewer
//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.setDataAndType(uri, "application/pdf")
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
//                            startActivity(intent)
//                        } else {
//                            Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Toast.makeText(context, "Could not open PDF: ${e.message}", Toast.LENGTH_LONG).show()
//                }
            }
        )

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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                LoadingUtils.showDialog(requireContext(),false)
                viewModel.myPrescription(type).collect{
                    when(it){
                        is NetworkResult.Success ->{
                            LoadingUtils.hideDialog()
                            var data = it.data
                            if (data != null) {
                                Log.d("TESTING_AWPL_DATA",data.size.toString())

                                data.forEach {
                                    Log.d("TESTING_AWPL_DATA",it.time.toString())
                                }

                                if(data.size ==0){
                                    binding.tvNoDataPres.visibility =View.VISIBLE
                                }else{
                                    binding.tvNoDataPres.visibility =View.GONE
                                }
                                adapter.updateAdapter(data)
                            }else{
                                binding.tvNoDataPres.visibility =View.VISIBLE
                            }
                        }
                        is NetworkResult.Error ->{
                            LoadingUtils.hideDialog()
                            LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
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