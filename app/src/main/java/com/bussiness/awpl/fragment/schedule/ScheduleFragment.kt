package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.VideoCallActivity
import com.bussiness.awpl.adapter.AppointmentAdapter
import com.bussiness.awpl.adapter.CancelledAdapter
import com.bussiness.awpl.adapter.CompletedAdapter
import com.bussiness.awpl.adapter.SymptomsUploadCompleteAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentScheduleBinding
import com.bussiness.awpl.model.AppointmentModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.DownloadWorker
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.MyAppointmentViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var viewModel: MyAppointmentViewModel
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var cancelledAdapter: CancelledAdapter
    private lateinit var completedAdapter: CompletedAdapter
    private lateinit var completedSymptomsAdapter :SymptomsUploadCompleteAdapter
    private var selectedTab = 0
    private var isSelected = false
    private var filter ="all"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        completedSymptomsAdapter = SymptomsUploadCompleteAdapter(mutableListOf()){ path->
            var url = AppConstant.Base_URL+path.file_path
            DownloadWorker().downloadPdfWithNotification(requireContext(),url,"Prescription_${System.currentTimeMillis()}.pdf")
            Toast.makeText(requireContext(),"Download Started",Toast.LENGTH_LONG).show()
        }
        viewModel = ViewModelProvider(this)[MyAppointmentViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        })

        clickListener()
        setUpRecyclerView()
        selectTab(selectedTab)
        callingUpcomingApi()
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
                    callingCompletedApi("all")
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
                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        it.data?.let { it1 -> cancelledAdapter.updateAdapter(it1) }
                    }
                    is NetworkResult.Error -> {
                           LoadingUtils.hideDialog()
                           LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else-> {

                    }
                }
            }
        }

    }

    private fun callingUpcomingApi(){
      if(viewModel.upcomingList.size > 0){

          if (viewModel.upcomingList.size > 0) {
              binding.noDataView.visibility = View.GONE
              binding.recyclerView.visibility = View.VISIBLE
              appointmentAdapter.updateAdapter(viewModel.upcomingList)
          } else {
              binding.apply {
                  noDataView.visibility = View.VISIBLE
                  recyclerView.visibility = View.GONE
              }
          }
          appointmentAdapter.updateAdapter(viewModel.upcomingList)
      }else {
          lifecycleScope.launch {
              LoadingUtils.showDialog(requireContext(), false)
              viewModel.upcomingAppoint().collect {
                  when (it) {
                      is NetworkResult.Success -> {
                          LoadingUtils.hideDialog()
                          var data = it.data
                          if (data != null && data.size> 0) {
                              Log.d("TESTING_SIZE", "Size of the list is " + data.size.toString())
                              if (data.size > 0) {
                                  binding.noDataView.visibility = View.GONE
                                  binding.recyclerView.visibility = View.VISIBLE
                                  viewModel.upcomingList = data
                                  appointmentAdapter.updateAdapter(viewModel.upcomingList)
                              } else {
                                  binding.apply {
                                      noDataView.visibility = View.VISIBLE
                                      recyclerView.visibility = View.GONE
                                  }
                              }
                          }
                          else {
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
                      else -> { }
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
                        }
                        else{
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
                var chatId = if(appointment.chat_id != null) appointment.chat_id else null
                var bundle =Bundle().apply {
                    putInt(AppConstant.AppoitmentId,appointment.id)

                    putString(AppConstant.Chat, chatId)
                }
                if(chatId != null){
                    findNavController().navigate(R.id.doctorChatFragment,bundle)
                }



            },
            onDownloadPrescriptionClick = { appointment ->
                // Handle download prescription button click
                Toast.makeText(requireContext(), "Download Prescription for ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
            })

        appointmentAdapter = AppointmentAdapter(
            mutableListOf(),
            onCancelClick = { appointment -> cancelDialog(appointment) },
            onRescheduleClick = {

                var bundle = Bundle().apply {
                    putInt(AppConstant.AppoitmentId,it.id)
                }
                findNavController().navigate(R.id.reschedule_call,bundle)

                                },
            onInfoClick = { _, infoIcon -> showInfoPopup(infoIcon) },
            startAppoitmentClick={ apoitnment -> openVideoCall(apoitnment)}
        )

        cancelledAdapter = CancelledAdapter(mutableListOf()) { appointment ->
            // Handle reschedule button click here
            var bundle =Bundle().apply {
                putInt(AppConstant.AppoitmentId,appointment.id)
            }
            findNavController().navigate(R.id.reschedule_call,bundle)
           // Toast.makeText(requireContext(), "Rescheduled: ${appointment.doctorName}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = completedAdapter
        }

    }

    private fun callingCallJoinedApi(startAppointment: Int, intent: Intent) {

        lifecycleScope.launch {
            viewModel.callJoined(startAppointment).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        Log.d("TESTING_CALL_JOINED","Calling joined call")
                        startActivity(intent)
                    }
                    is NetworkResult.Error ->{
                        Log.d("TESTING_CALL_JOINED","Calling joined cancel")
                        LoadingUtils.hideDialog()
                    }
                    else ->{
                    }
                }
            }
        }
    }

    private fun openVideoCall(model :UpcomingModel){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.createChannel(model.id).collect {
                when(it){
                    is NetworkResult.Success ->{
                        it.data?.let {
                            val intent = Intent(requireContext(), VideoCallActivity::class.java)
                            intent.putExtra(AppConstant.APPID,it.appId)
                            intent.putExtra(AppConstant.AuthToken,it.token)
                            intent.putExtra(AppConstant.CHANNEL_NAME,it.channelName)
                            intent.putExtra(AppConstant.uid,it.uid)
                            intent.putExtra(AppConstant.DOCTOR,model.doctorName)
                            //  startActivity(intent)
                          callingCallJoinedApi(model.id,intent)
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

    private fun clickListener() {

        binding.scheduleCall1.setOnClickListener {
            binding.scheduleCall1.setBackgroundResource(R.drawable.bg_four_side_corner_inner_white)
            binding.tv2.setTextColor("#858484".toColorInt())
            binding.scheduleCall2.background = null
            binding.tV1.setTextColor("#356598".toColorInt())
            binding.filterBtn.visibility =View.VISIBLE
            completedAdapter.update(true, mutableListOf())
            binding.recyclerView.adapter = completedAdapter
            callingCompletedApi(filter)
        }

        binding.scheduleCall2.setOnClickListener {
            binding.scheduleCall2.setBackgroundResource(R.drawable.bg_four_side_corner_inner_white)
            binding.tV1.setTextColor(android.graphics.Color.parseColor("#858484"))
            binding.scheduleCall1.background = null
            binding.filterBtn.visibility =View.GONE
            binding.recyclerView.adapter  = completedSymptomsAdapter
            binding.tv2.setTextColor(android.graphics.Color.parseColor("#356598"))
            completedAdapter.update(false, mutableListOf())
            completedSymptomsUpload()

        }

        binding.apply {
            txtUpcoming.setOnClickListener { selectTab(0) }
            txtCompleted.setOnClickListener { selectTab(1) }
            txtCanceled.setOnClickListener { selectTab(2) }
            btnNext.setOnClickListener { findNavController().navigate(R.id.diseasesBottomFragment) }
            filterBtn.setOnClickListener {
                isSelected = !isSelected // Toggle state
                if (isSelected) {
                    filterPopUp(it)
                }
            }
        }
    }

    private fun completedSymptomsUpload(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.completedSymptomsUpload().collect {
                when (it) {
                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        Log.d("TESTING_SIZE_ANDROID",it.data?.size.toString())
                        it.data?.let { it1 -> completedSymptomsAdapter.updateAdapter(it1) }
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


    @SuppressLint("InflateParams")
    private fun filterPopUp(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_filter, null)

        val popupWindow = PopupWindow(
            popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )
       var forMeCheckBox = popupView.findViewById<MaterialCheckBox>(R.id.appCompatCheckBox)
        var forOtherBox = popupView.findViewById<androidx.appcompat.widget.AppCompatCheckBox>(R.id.appCompatCheckBox2)


        if(filter == "me"){
            forMeCheckBox.isChecked = true
        }
        else if(filter == "others"){
            forOtherBox.isChecked = true
        }

        forMeCheckBox.setOnClickListener {
            if(forMeCheckBox.isChecked){
                forOtherBox.isChecked = false
                filter ="me"
                callingCompletedApi("me")
            }else{
                filter ="all"
                callingCompletedApi("all")
            }
        }

        forOtherBox.setOnClickListener {
            if(forOtherBox.isChecked) {
                forMeCheckBox.isChecked = false
                filter ="others"
                callingCompletedApi("others")
            }else{
                filter="all"
                callingCompletedApi("all")
            }
        }


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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
