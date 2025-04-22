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

class DoctorConsultationFragment : Fragment() {

    private var _binding: FragmentDoctorConsultationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorConsultationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
    }

    private fun clickListeners() {
        binding.apply {
            cancelBtn.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
            proceedBtn.setOnClickListener { findNavController().navigate(R.id.appointmentBooking) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
