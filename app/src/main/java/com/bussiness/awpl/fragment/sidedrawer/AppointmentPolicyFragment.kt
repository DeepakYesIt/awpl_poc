package com.bussiness.awpl.fragment.sidedrawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bussiness.awpl.databinding.FragmentAppointmentPolicyBinding

class AppointmentPolicyFragment : Fragment() {

    private var _binding: FragmentAppointmentPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl("file:///android_asset/appointment_policy.html")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
