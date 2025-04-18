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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.AppointmentAdapter
import com.bussiness.awpl.adapter.CancelledAdapter
import com.bussiness.awpl.adapter.CompletedAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentScheduleBinding
import com.bussiness.awpl.model.AppointmentModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var cancelledAdapter: CancelledAdapter
    private lateinit var completedAdapter: CompletedAdapter
    private var selectedTab = 0
    private var isSelected = false
    private val appointmentList = listOf(
        AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Scheduled Call Consultations","", R.drawable.doctor_bg_icon),
        AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
        AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
        AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
        AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Scheduled Call Consultations","", R.drawable.doctor_bg_icon),
        AppointmentModel("Dr. John Doe", "Thu May 14","10:00 - 10:15 AM","Symptom Upload Consultations","Thu May 14", R.drawable.doctor_bg_icon),
    )
    private val appointments = listOf(
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showTemporaryData()
        clickListener()
        setUpRecyclerView()
        selectTab(selectedTab)
    }

    private fun selectTab(index: Int) {
        selectedTab = index

        binding.apply {
            // Reset colors and hide underline for all tabs
            txtUpcoming.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))
            txtCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))
            txtCanceled.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))

            viewUpcoming.visibility = View.INVISIBLE
            viewCompleted.visibility = View.INVISIBLE
            viewCanceled.visibility = View.INVISIBLE

            // Highlight selected tab and set adapter
            when (index) {
                0 -> {
                    txtUpcoming.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewUpcoming.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                    view.visibility = View.VISIBLE
                    binding.recyclerView.adapter = appointmentAdapter
                }
                1 -> {
                    txtCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewCompleted.visibility = View.VISIBLE
                    filterBtn.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                    binding.recyclerView.adapter = completedAdapter
                }
                2 -> {
                    txtCanceled.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewCanceled.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                    view.visibility = View.VISIBLE
                    binding.recyclerView.adapter = cancelledAdapter
                }
            }
        }
    }


    private fun setUpRecyclerView() {

        completedAdapter = CompletedAdapter(appointmentList,
            onCheckDetailsClick = { appointment ->
                findNavController().navigate(R.id.doctorChatFragment)
            },
            onDownloadPrescriptionClick = { appointment ->
                // Handle download prescription button click
                Toast.makeText(requireContext(), "Download Prescription for ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
            })

        appointmentAdapter = AppointmentAdapter(
            appointments.toMutableList(),
            onCancelClick = { appointment -> cancelDialog(appointment) },
            onRescheduleClick = { Toast.makeText(requireContext(), "Reschedule clicked", Toast.LENGTH_SHORT).show() },
            onInfoClick = { _, infoIcon -> showInfoPopup(infoIcon) }
        )

        cancelledAdapter = CancelledAdapter(appointments) { appointment ->
            // Handle reschedule button click here
            Toast.makeText(requireContext(), "Rescheduled: ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = completedAdapter
        }

    }

    private fun clickListener() {
        binding.apply {
            txtUpcoming.setOnClickListener { selectTab(0) }
            txtCompleted.setOnClickListener { selectTab(1) }
            txtCanceled.setOnClickListener { selectTab(2) }
            btnNext.setOnClickListener { findNavController().navigate(R.id.appointmentBooking) }
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

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Handle dismiss listener to reset UI
        popupWindow.setOnDismissListener {
            isSelected = false
            binding.filterBtn.setBackgroundResource(R.drawable.filter_bg)
            binding.filterTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
            binding.arrowIcon.setImageResource(R.drawable.short_arrow)
        }

        // Show popup below the clicked view
        popupWindow.showAsDropDown(anchorView, 0, 10)

        popupView.setOnClickListener {
            popupWindow.dismiss()
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
                findNavController().navigate(R.id.appointmentBooking)
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

    private fun showTemporaryData() {
        binding.apply {
            noDataView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            lifecycleScope.launch {
                delay(3000)
                noDataView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
