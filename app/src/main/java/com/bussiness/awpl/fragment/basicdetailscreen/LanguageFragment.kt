package com.bussiness.awpl.fragment.basicdetailscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentWelcome2Binding
import com.bussiness.awpl.utils.SessionManager

class LanguageFragment : Fragment() {

    private var _binding: FragmentWelcome2Binding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome2Binding.inflate(inflater, container, false)

        // Initialize SessionManager
        sessionManager = SessionManager(requireContext())

        clickListener()
        return binding.root
    }

    private fun clickListener() {
        binding.apply {
            // English button click - Set language to English and navigate to the next screen
            englishLangBtn.setOnClickListener {
                sessionManager.setLanguage("en")  // Set language to English
                sessionManager.changeLanguage(requireContext(), "en")  // Apply the language change
                findNavController().navigate(R.id.onBoardingFragment)  // Navigate to next screen
            }

            // Hindi button click - Set language to Hindi and navigate to the next screen
            hindiLangBtn.setOnClickListener {
                sessionManager.setLanguage("hi")  // Set language to Hindi
                sessionManager.changeLanguage(requireContext(), "hi")  // Apply the language change
                findNavController().navigate(R.id.onBoardingFragment)  // Navigate to next screen
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
