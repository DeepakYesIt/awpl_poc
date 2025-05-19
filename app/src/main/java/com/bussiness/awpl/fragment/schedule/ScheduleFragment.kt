package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.AppointmentAdapter
import com.bussiness.awpl.adapter.CancelledAdapter
import com.bussiness.awpl.adapter.CompletedAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentScheduleBinding
import com.bussiness.awpl.model.AppointmentModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.MyAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var viewModel: MyAppointmentViewModel
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
        viewModel = ViewModelProvider(this)[MyAppointmentViewModel::class.java]
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
                    upperLay.visibility = View.GONE
                    view.visibility = View.VISIBLE
                    binding.recyclerView.adapter = appointmentAdapter
                    callingUpcomingApi()
                }
                1 -> {
                    txtCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewCompleted.visibility = View.VISIBLE
                    filterBtn.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                    upperLay.visibility = View.VISIBLE
                    binding.recyclerView.adapter = completedAdapter
                }
                2 -> {
                    txtCanceled.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewCanceled.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                    upperLay.visibility = View.GONE
                    view.visibility = View.VISIBLE
                    binding.recyclerView.adapter = cancelledAdapter
                    cancelApiCall()
                }
            }
        }
    }


    private fun cancelApiCall(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.cancelAppointment().collect{
                when(it){
                    is NetworkResult.Success ->{

                    }
                    is NetworkResult.Error ->{

                    }
                    else->{

                    }
                }
            }
        }

    }

    private fun callingUpcomingApi(){
      if(viewModel.upcomingList.size > 0){
          appointmentAdapter.updateAdapter(viewModel.upcomingList)
      }else {
          lifecycleScope.launch {
              LoadingUtils.showDialog(requireContext(), false)
              viewModel.upcomingAppoint().collect {
                  when (it) {
                      is NetworkResult.Success -> {
                          LoadingUtils.hideDialog()
                          var data = it.data
                          if (data != null) {
                              Log.d("TESTING_SIZE", "Size of the list is " + data.size.toString())
                              if (data.size > 0) {
                                  binding.noDataView.visibility = View.GONE
                                  binding.recyclerView.visibility = View.VISIBLE
                                  appointmentAdapter.updateAdapter(data)
                              } else {
                                  binding.apply {
                                      noDataView.visibility = View.VISIBLE
                                      recyclerView.visibility = View.GONE
                                  }
                              }
                          } else {
                              binding.apply {
                                  noDataView.visibility = View.VISIBLE
                                  recyclerView.visibility = View.GONE
                              }
                          }
                      }

                      is NetworkResult.Error -> {
                          LoadingUtils.hideDialog()
                          LoadingUtils.showErrorDialog(requireContext(), it.message.toString())
                      }

                      else -> {

                      }
                  }
              }
          }
      }
    }

    private fun callingCompletedApi(type:String){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.completedAppointment(type).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        var data = it.data
                        if (data != null) {
                            Log.d("TESTING_SIZE","Size of the list is "+data.size.toString())
                            if(data.size > 0) {
                                binding.noDataView.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                completedAdapter.updateAdapter(data)
                            }else{
                                binding.apply {
                                    noDataView.visibility = View.VISIBLE
                                    recyclerView.visibility = View.GONE
                                }
                            }
                        }else{
                            binding.apply {
                                noDataView.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            }
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {

        completedAdapter = CompletedAdapter(
            mutableListOf(),
            onCheckDetailsClick = { appointment ->
                findNavController().navigate(R.id.doctorChatFragment)
            },
            onDownloadPrescriptionClick = { appointment ->
                // Handle download prescription button click
                Toast.makeText(requireContext(), "Download Prescription for ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
            })

        appointmentAdapter = AppointmentAdapter(
            mutableListOf(),
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

        binding.scheduleCall1.setOnClickListener {
            binding.scheduleCall1.setBackgroundResource(R.drawable.bg_four_side_corner_inner_white)
            binding.tv2.setTextColor(android.graphics.Color.parseColor("#858484"))
            binding.scheduleCall2.background = null
            binding.tV1.setTextColor(android.graphics.Color.parseColor("#356598"))

           completedAdapter.update(true, mutableListOf())
        }

        binding.scheduleCall2.setOnClickListener {
            binding.scheduleCall2.setBackgroundResource(R.drawable.bg_four_side_corner_inner_white)
            binding.tV1.setTextColor(android.graphics.Color.parseColor("#858484"))
            binding.scheduleCall1.background = null

            binding.tv2.setTextColor(android.graphics.Color.parseColor("#356598"))
            completedAdapter.update(false, mutableListOf())
        }

        binding.apply {
            txtUpcoming.setOnClickListener { selectTab(0) }
            txtCompleted.setOnClickListener { selectTab(1) }
            txtCanceled.setOnClickListener { selectTab(2) }
            btnNext.setOnClickListener { findNavController().navigate(R.id.appointmentBooking) }
            filterBtn.setOnClickListener {
                isSelected = !isSelected // Toggle state
                if (isSelected) {
                    filterPopUp(it)
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun filterPopUp(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_filter, null)

        val popupWindow = PopupWindow(
            popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )

        // Handle dismiss listener to reset UI
        popupWindow.setOnDismissListener {
            isSelected = false

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
    private fun cancelDialog(appointment: UpcomingModel) {
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

//            lifecycleScope.launch {
//                delay(3000)
//                noDataView.visibility = View.GONE
//                recyclerView.visibility = View.VISIBLE
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
