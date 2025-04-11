package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.AppointmentAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentUpcomingBinding
import com.bussiness.awpl.model.AppointmentModel

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val appointments = listOf(
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
            AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM", "","",R.drawable.doctor_bg_icon),
        )

        appointmentAdapter = AppointmentAdapter(
            appointments.toMutableList(),
            onCancelClick = { appointment -> cancelDialog(appointment) },
            onRescheduleClick = { Toast.makeText(requireContext(), "Reschedule clicked", Toast.LENGTH_SHORT).show() },
            onInfoClick = { _, infoIcon -> showInfoPopup(infoIcon) }
        )

        binding.appointmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appointmentAdapter
        }
    }

    private fun showInfoPopup(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_popup_info_icon, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // Dismiss when clicking outside
        ).apply { showAsDropDown(anchorView, 0, 10) }
        popupView.setOnClickListener { popupWindow.dismiss() }
    }

    @SuppressLint("SetTextI18n")
    private fun cancelDialog(appointment: AppointmentModel) {
        val dialog = Dialog(requireContext())
        val binding = DialogCancelAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNo.setOnClickListener { dialog.dismiss() }
            btnYes.setOnClickListener {
                appointmentAdapter.removeAppointment(appointment)
                dialog.dismiss()
            }
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
