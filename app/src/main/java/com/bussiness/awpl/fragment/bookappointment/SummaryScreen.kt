package com.bussiness.awpl.fragment.bookappointment

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.SummaryAdapter
import com.bussiness.awpl.databinding.DialogConfirmAppointmentBinding
import com.bussiness.awpl.databinding.DialogCongratulationsBinding
import com.bussiness.awpl.databinding.FragmentSummaryScreenBinding
import com.bussiness.awpl.model.SummaryModel

class SummaryScreen : Fragment() {
    private var _binding: FragmentSummaryScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickListener()
    }

    private fun setupRecyclerView() {
        val doctorList = listOf(
            SummaryModel(R.drawable.doctor_image, "Dr. Aman Sharma", "Experience: 5 Years"),
            SummaryModel(R.drawable.doctor_image, "Dr. Priya Mehta", "Experience: 8 Years"),
            SummaryModel(R.drawable.doctor_image, "Dr. Rahul Verma", "Experience: 10 Years")
        )

        val adapter = SummaryAdapter(doctorList)
        binding.recyclerViewDoctors.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun clickListener() {
        binding.apply {
            btnNext.setOnClickListener { congratsDialog() }
            arrowIcon.setOnClickListener {
                if(edtPromoCode.text.toString().isNotEmpty()){
                    applyPromoConstraintLayout.visibility = View.GONE
                    promoValidate.visibility = View.VISIBLE
                }
            }
            txtPrivacyPolicy.setOnClickListener { findNavController().navigate(R.id.privacyPolicyFragment) }
            txtTermsConditions.setOnClickListener { findNavController().navigate(R.id.termsAndConditionFragment) }
        }
    }

    private fun congratsDialog(){
        val dialog = Dialog(requireContext())
        val binding = DialogCongratulationsBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNext.setOnClickListener { findNavController().navigate(R.id.paymentScreen)
                dialog.dismiss() }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
