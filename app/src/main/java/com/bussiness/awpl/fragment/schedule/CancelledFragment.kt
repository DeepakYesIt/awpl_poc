package com.bussiness.awpl.fragment.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.CancelledAdapter
import com.bussiness.awpl.databinding.FragmentCancelledBinding
import com.bussiness.awpl.model.AppointmentModel

class CancelledFragment : Fragment() {

    private var _binding: FragmentCancelledBinding? = null
    private val binding get() = _binding!!
    private lateinit var cancelledAdapter: CancelledAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancelledBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val appointments = listOf(
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","" ,"",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","" ,"",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","" ,"",R.drawable.doctor_bg_icon),
        )
        cancelledAdapter = CancelledAdapter(appointments) { appointment ->
            // Handle reschedule button click here
            Toast.makeText(requireContext(), "Rescheduled: ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
        }
        binding.cancelledRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cancelledAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
