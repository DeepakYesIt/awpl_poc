package com.bussiness.awpl.fragment.sidedrawer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapters.NotificationAdapter
import com.bussiness.awpl.databinding.FragmentNotificationBinding
import com.bussiness.awpl.model.NotificationModel

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var mainActivity: HomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as HomeActivity
//        mainActivity.setUpToolBarIconText("notification")

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val notifications = listOf(
            NotificationModel("Appointment Success", "You have successfully booked an appointment with Dr. Emily Walker.", "1h", R.drawable.calendar_tick, "2025-03-25"),
            NotificationModel("Appointment Cancelled", "You have successfully cancelled your appointment with Dr. David Patel.", "2h", R.drawable.calendar_cancel_ic, "2025-03-25"),
            NotificationModel("Scheduled Changed", "You have successfully changed your appointment with Dr. Jessica Turner.", "6h", R.drawable.calendar_edit, "2025-03-25"),
            NotificationModel("Appointment Success", "You have successfully booked an appointment with Dr. David Patel.", "1d", R.drawable.calendar_tick, "2025-03-24"),
            NotificationModel("Appointment Cancelled", "You have successfully cancelled your appointment with Dr. David Patel.", "2d", R.drawable.calendar_cancel_ic, "2025-03-26")
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationAdapter = NotificationAdapter(notifications)
        binding.recyclerView.adapter = notificationAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? HomeActivity)?.findViewById<View>(R.id.homeBottomNav)?.visibility = View.VISIBLE
        _binding = null
    }
}
