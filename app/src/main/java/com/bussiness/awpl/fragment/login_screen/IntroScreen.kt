package com.bussiness.awpl.fragment.login_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentWelcome1Binding

class IntroScreen : Fragment() {

    private var _binding: FragmentWelcome1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome1Binding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        binding.apply {
            iconLanguage.setOnClickListener {
                findNavController().navigate(R.id.welcomeScreen2)
            }
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.welcomeScreen3)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
