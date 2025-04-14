package com.bussiness.awpl.fragment.basicdetailscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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
            englishLangBtn.setOnClickListener {
                sessionManager.setLanguage("en")
                sessionManager.changeLanguage(requireContext(), "en")
                findNavController().navigate(R.id.onBoardingFragment)
            }

            hindiLangBtn.setOnClickListener {
                sessionManager.setLanguage("hi")
                sessionManager.changeLanguage(requireContext(), "hi")
                findNavController().navigate(R.id.onBoardingFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
