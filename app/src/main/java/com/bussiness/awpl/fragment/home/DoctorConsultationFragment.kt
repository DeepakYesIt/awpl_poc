package com.bussiness.awpl.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.FragmentDoctorConsultationBinding
import com.bussiness.awpl.utils.AppConstant

class DoctorConsultationFragment : Fragment() {

    private var _binding: FragmentDoctorConsultationBinding? = null
    private val binding get() = _binding!!
    private var diseaseId :Int =0
    private var type :String =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorConsultationBinding.inflate(inflater, container, false)

        arguments?.let {
            if(it.containsKey(AppConstant.DISEASE_ID)){
                diseaseId = it.getInt(AppConstant.DISEASE_ID)
                type = it.getString(AppConstant.TYPE).toString()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
    }

    private fun clickListeners() {
        binding.apply {
            cancel.setOnClickListener { findNavController().navigate(R.id.homeFragment) }

            proceedBtn.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt(AppConstant.DISEASE_ID , diseaseId)
                    putString(AppConstant.TYPE , type)
                }
                findNavController().navigate(R.id.homeScheduleCallFragment,bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
