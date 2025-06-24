package com.bussiness.awpl.fragment.sidedrawer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.NotificationAdapter
import com.bussiness.awpl.databinding.FragmentNotificationBinding
import com.bussiness.awpl.model.NotificationModel
import com.bussiness.awpl.model.PatinetNotification
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationViewModel: NotificationViewModel
    private var notificationList = mutableListOf<PatinetNotification>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        notificationViewModel = ViewModelProvider(requireActivity())[NotificationViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        callingNotificationApi()
        binding.markAsRead.setOnClickListener {
            callingMarkAllReadApi()
        }
    }

    private fun callingMarkAllReadApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            notificationViewModel.markAllRead().collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        notificationList.forEach { it.readStatus = "read" }
                        notificationAdapter.updateAdapter(notificationList)
                        LoadingUtils.showSuccessDialog(requireContext(),it.data.toString())
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

    private fun callingNotificationApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            notificationViewModel.patientNotification().collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        var arr = it.data
                        arr?.forEach {
                            if(isToday(it.date)){
                                it.date = "Today"
                            }
                        }
                        var date =""
                        var newList = mutableListOf<PatinetNotification>()
                        arr?.forEach {
                             if(!it.date.equals(date)){
                                 date = it.date
                                 var data = PatinetNotification(date, "","","","","","")
                                 newList.add(data)
                                 newList.add(it)
                             }else{
                                 newList.add(it)
                             }
                        }
                        Log.d("TESTING_LIST_SIZE",newList.size.toString())
                        if(newList == null || newList.size ==0){
                            binding.tvNoData.visibility =View.VISIBLE
                        }else{
                            binding.tvNoData.visibility = View.GONE
                        }
                        notificationList = newList
                        notificationAdapter.updateAdapter(newList)
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

    fun isToday(dateString: String): Boolean {
        val formatter = SimpleDateFormat("EEE MMM dd", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getDefault() // Adjust if needed

        return try {
            val inputDate = formatter.parse(dateString)

            val calendarInput = Calendar.getInstance()
            calendarInput.time = inputDate!!

            val calendarToday = Calendar.getInstance()

            calendarInput.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR) &&
                    calendarInput.get(Calendar.DAY_OF_YEAR) == calendarToday.get(Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            false
        }
    }


    private fun setupRecyclerView() {

//        val notifications = listOf(
//            NotificationModel("Appointment Success", "You have successfully booked an appointment with Dr. Emily Walker.", "1h", R.drawable.calendar_tick, "2025-03-25"),
//            NotificationModel("Appointment Cancelled", "You have successfully cancelled your appointment with Dr. David Patel.", "2h", R.drawable.calendar_cancel_ic, "2025-03-25"),
//            NotificationModel("Scheduled Changed", "You have successfully changed your appointment with Dr. Jessica Turner.", "6h", R.drawable.calendar_edit, "2025-03-25"),
//            NotificationModel("Appointment Success", "You have successfully booked an appointment with Dr. David Patel.", "1d", R.drawable.calendar_tick, "2025-03-24"),
//            NotificationModel("Appointment Cancelled", "You have successfully cancelled your appointment with Dr. David Patel.", "2d", R.drawable.calendar_cancel_ic, "2025-03-26")
//        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationAdapter = NotificationAdapter(mutableListOf()){ id->
            lifecycleScope.launch {
                notificationViewModel.markNotificationRead(id).collect {
                       when(it){
                           is NetworkResult.Success ->{
                               val index = notificationList.indexOfFirst { it.id == id }
                               if (index != -1) {
                                   val patientNotification = notificationList[index].copy(readStatus = "read")
                                   notificationList[index] = patientNotification
                                   notificationAdapter.updateAdapter(notificationList)
                               }
                           }
                           is NetworkResult.Error ->{

                           }
                           else ->{

                           }
                       }
                }
            }
        }
        binding.recyclerView.adapter = notificationAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
