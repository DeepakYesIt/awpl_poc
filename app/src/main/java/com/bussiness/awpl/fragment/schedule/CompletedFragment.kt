package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.CompletedAdapter
import com.bussiness.awpl.databinding.FragmentCompletedBinding
import com.bussiness.awpl.model.AppointmentModel

class CompletedFragment : Fragment() {

    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!
    private lateinit var completedAdapter: CompletedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickListener()
    }

    private fun setupRecyclerView() {
        val appointmentList = listOf(
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Scheduled Call Consultations","", R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Scheduled Call Consultations","", R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),

        )

        completedAdapter = CompletedAdapter(appointmentList,
            onCheckDetailsClick = { appointment ->
                findNavController().navigate(R.id.doctorChatFragment)
            },
            onDownloadPrescriptionClick = { appointment ->
                // Handle download prescription button click
                Toast.makeText(requireContext(), "Download Prescription for ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
            })

        binding.completeRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = completedAdapter
        }
    }

    private fun clickListener() {
        binding.apply {
            var isSelected = false

            filterBtn.setOnClickListener {
                isSelected = !isSelected // Toggle state

                if (isSelected) {
                    filterBtn.setBackgroundResource(R.drawable.button_bg)
                    filterTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    arrowIcon.setImageResource(R.drawable.up_arrow)
                    filterPopUp(it)
                } else {
                    filterBtn.setBackgroundResource(R.drawable.filter_bg)
                    filterTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    arrowIcon.setImageResource(R.drawable.short_arrow)
                }
            }
        }

    }

    @SuppressLint("InflateParams")
    private fun filterPopUp(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_filter, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true )

        // Show popup below the clicked view (info icon)
        popupWindow.showAsDropDown(anchorView, 0, 10)

        // Optional: Dismiss popup when clicking on it
        popupView.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
