package com.bussiness.awpl.fragment.sidedrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.CancelledAdapter
import com.bussiness.awpl.databinding.FragmentIncompleteAppointmentBinding
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.utils.AppConstant

class IncompleteAppointmentFragment : Fragment() {

    private var _binding: FragmentIncompleteAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var cancelledAdapter: CancelledAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncompleteAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        cancelledAdapter.updateAdapter(getDummyAppointments()) // update with static or API data
    }

    private fun setUpRecyclerView() {
        cancelledAdapter = CancelledAdapter(mutableListOf()) { appointment ->
            val bundle = Bundle().apply {
                putInt(AppConstant.AppoitmentId, appointment.id)
            }
//            findNavController().navigate(R.id.reschedule_call, bundle)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cancelledAdapter
        }
    }

    private fun getDummyAppointments(): List<CancelledAppointment> {
        return listOf(
            CancelledAppointment(
                id = 1,
                doctorName = "Dr. Anjali Sharma",
                doctorImage = "/images/anjali.jpg",
                date = "2025-06-20",
                time = "11:00 AM",
                patient_id = 1
            ),
            CancelledAppointment(
                id = 2,
                doctorName = "Dr. Rajeev Singh",
                doctorImage = "/images/rajeev.jpg",
                date = "2025-06-22",
                time = "03:30 PM",
                patient_id = 2
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

