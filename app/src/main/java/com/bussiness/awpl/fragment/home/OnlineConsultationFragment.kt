package com.bussiness.awpl.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.activities.OnBoardActivity
import com.bussiness.awpl.databinding.FragmentOnlineConsultationBinding

class OnlineConsultationFragment : Fragment() {

    private var _binding: FragmentOnlineConsultationBinding? = null
    private val binding get() = _binding!!
    private var mainActivity: HomeActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineConsultationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as? HomeActivity
//        mainActivity?.setUpToolBarIconText("online_consultation")

        clickListeners()
    }

    private fun clickListeners() {
        binding.apply {
            cancelBtn.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
            proceedBtn.setOnClickListener { findNavController().navigate(R.id.symptomUpload) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (requireActivity() as? HomeActivity)?.findViewById<View>(R.id.homeBottomNav)?.visibility = View.VISIBLE
        _binding = null
    }
}
