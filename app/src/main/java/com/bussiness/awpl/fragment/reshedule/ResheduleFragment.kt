package com.bussiness.awpl.fragment.reshedule

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.TimeSlotAdapter
import com.bussiness.awpl.databinding.FragmentResheduleBinding

import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.BookingViewModel
import com.bussiness.awpl.viewmodel.RescheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class ResheduleFragment : Fragment() {


    private var binding : FragmentResheduleBinding? = null
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var bookingViewModel : BookingViewModel
    private  var callId =0
    var disabledDates = mutableListOf<LocalDate>()
    private lateinit var rescheduleViewModel : RescheduleViewModel
    private var selectTime =""
    private var currentMonth: YearMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        YearMonth.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate? = LocalDate.now()
    private  var selectedDateStr =""

    val timeSlots = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rescheduleViewModel = ViewModelProvider(requireActivity())[RescheduleViewModel::class.java]
        arguments?.let {
             if(it.containsKey(AppConstant.AppoitmentId)){
                 callId = it.getInt(AppConstant.AppoitmentId)
             }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  FragmentResheduleBinding.inflate(inflater, container, false)
        bookingViewModel= ViewModelProvider(requireActivity())[BookingViewModel::class.java]

        setupRecyclerView()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = today.format(formatter)
        selectedDateStr = formattedDate
        bookingViewModel.selectedDateStr = selectedDateStr
        callingMakeTimeSlot(formattedDate)
        clickListener()
        updateCalendar()

        val formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        disabledDates = SessionManager(requireContext()).getHolidayList()
            .mapNotNull {
                try {
                    LocalDate.parse(it.date, formatter1)
                } catch (e: Exception) {
                    null
                }
            }.toMutableList()

        if(SessionManager(requireContext()).getHolidayList().isEmpty()){
            callingGetHolidayListApi()
        }

        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callingGetHolidayListApi(){
        lifecycleScope.launch {

            bookingViewModel.getHolidayList().collect(){
                when(it){
                    is NetworkResult.Success->{
                        val formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        LoadingUtils.hideDialog()
                        it.data?.let {
                            disabledDates = it.mapNotNull {
                                try {
                                    LocalDate.parse(it.date, formatter1)
                                } catch (e: Exception) {
                                    null
                                }
                            }.toMutableList()
                            updateCalendar()
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                    }
                    else ->{

                    }
                }
            }

        }
    }


    private fun callingMakeTimeSlot(date:String){

//        for (hour in 0..23) {
//            for (minute in listOf(0, 15, 30, 45)) {
//                val amPm = if (hour < 12) "AM" else "PM"
//                val hourFormatted = when {
//                    hour == 0 -> 12
//                    hour > 12 -> hour - 12
//                    else -> hour
//                }
//                val time = String.format("%02d:%02d %s", hourFormatted, minute, amPm)
//                timeSlots.add(time)
//            }
//        }
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            bookingViewModel.getScheduleTime(date).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        timeSlots.clear()
                        it.data?.let { it1 -> timeSlots.addAll(it1.timeSlotList)

                            Log.d("TESTING_SIZE",timeSlots.size.toString()+" from local")
                            timeSlotAdapter.updateAdapter(timeSlots)
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                    }
                    else ->{

                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.recyclerViewTimeSlots?.apply {
            this?.layoutManager = this@ResheduleFragment.layoutManager
            timeSlotAdapter = TimeSlotAdapter(timeSlots) { selectedTime ->
                binding?.textView25?.text = "Selected: $selectedTime"
                selectTime = selectedTime
                bookingViewModel.selectTime = selectedTime
            }
            adapter = timeSlotAdapter
        }
    }


    private fun clickListener() {
        binding?.apply {
            leftBtnPrev.setOnClickListener {
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItem > 0) {
                    layoutManager.smoothScrollToPosition(binding?.recyclerViewTimeSlots, null, firstVisibleItem - 1)
                }
            }
            rightBtnNext.setOnClickListener {
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem < timeSlots.size - 1) {
                    layoutManager.smoothScrollToPosition(binding?.recyclerViewTimeSlots, null, lastVisibleItem + 1)
                }
            }
            btnNext.setOnClickListener {
                callingBookingSlotApi()

            }

        }
    }

    private fun callingBookingSlotApi(){
        lifecycleScope.launch {
            if(!selectTime.isEmpty()) {
                LoadingUtils.showDialog(requireContext(),false)
               var tIMEHR = SessionManager(requireContext()).to24Hour(selectTime)
                Log.d("TIME",bookingViewModel.selectedDateStr +" "+tIMEHR +" "+callId)
                LoadingUtils.showDialog(requireContext(),false)
                rescheduleViewModel.resheduleAppointment(callId,bookingViewModel.selectedDateStr,tIMEHR).collect{
                    when(it){
                        is NetworkResult.Success ->{
                            LoadingUtils.hideDialog()
                            LoadingUtils.showSuccessDialog(requireContext(),it.data.toString()){
                                findNavController().navigate(R.id.scheduleFragment)
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

            }else{
                LoadingUtils.showErrorDialog(requireContext(),"Please select an appointment time.")
            }
        }
    }


    private fun updateCalendar() {
        // Updates the calendar layout with the current and next month views.
        val calendarLayout = binding?.calendarLayout
        calendarLayout?.removeAllViews()
        val topMonths = mutableListOf<YearMonth>()
        val bottomMonths = mutableListOf<YearMonth>()

        // Separate months into top and bottom lists
        val allMonths = (1..12).map { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentMonth.plusMonths(it.toLong())
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        }
        allMonths.forEachIndexed { index, month ->
            if (index % 2 == 0) {
                topMonths.add(month)
//            } else {
//                bottomMonths.add(month)
            }
        }

        if (calendarLayout != null) {
            addMonthView(calendarLayout, currentMonth)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun addMonthView(parentLayout: LinearLayout, yearMonth: YearMonth) {
        // Adds a view for the specified month to the parent layout.

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 10, 10, 10)
        val monthView = layoutInflater.inflate(R.layout.calendar_month, parentLayout, false)

        val monthTitle = monthView.findViewById<TextView>(R.id.month_title)
        val year = monthView.findViewById<TextView>(R.id.year_title)
        monthTitle.text = yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
        year.text = yearMonth.year.toString()

        //${yearMonth.year}"

        val daysLayout = monthView.findViewById<LinearLayout>(R.id.days_layout)
        daysLayout.removeAllViews()
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            val dayView = layoutInflater.inflate(R.layout.calendar_day_name, daysLayout, false) as TextView
            dayView.text = day
            daysLayout.addView(dayView)
        }

        val weeksLayout = monthView.findViewById<LinearLayout>(R.id.weeks_layout)
        weeksLayout.removeAllViews()
        val weeks = generateCalendarWeeks(yearMonth)
        weeks.forEach { week ->
            val weekLayout = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }
            week.forEach { date ->
                val dateView = layoutInflater.inflate(R.layout.calendar_day, weekLayout, false) as TextView
                if (date != null) {
                    dateView.text = date.dayOfMonth.toString().padStart(2, '0')

                    // Disable past dates
                    if (date.isBefore(LocalDate.now())
                        || date.dayOfWeek == DayOfWeek.SUNDAY
                        || disabledDates.contains(date)  ) {
                        dateView.isEnabled = false

                        dateView.setTextColor(Color.RED)
                    } else {
                        dateView.setOnClickListener {
                            selectedDate = date
                            selectedDateStr = selectedDate.toString()
                            bookingViewModel.selectedDateStr = selectedDateStr
                            updateCalendar()
                            callingMakeTimeSlot(selectedDate.toString())
                        }
                    }

                    when (date) {
                        selectedDate -> {
                            dateView.setBackgroundResource(R.drawable.selected_day_bg)
                            dateView.setTextColor(resources.getColor(R.color.white))
                        }
                        else -> {
                            dateView.setBackgroundResource(R.drawable.date_bg)
                            dateView.setTextColor(
                                if (date.isBefore(LocalDate.now())) Color.LTGRAY
                                else if (date.dayOfWeek == DayOfWeek.SUNDAY) Color.LTGRAY
                                else if(disabledDates.contains(date)) Color.LTGRAY
                                else if (date.month == yearMonth.month) Color.BLACK
                                else Color.GRAY
                            )
                        }
                    }
                }

                weekLayout.addView(dateView)
            }
            weeksLayout.addView(weekLayout)
        }
        // Display llPreviousAndNextMonth only for the current month
        if (yearMonth == currentMonth) {
            val previous = monthView.findViewById<ImageButton>(R.id.button_previous)
            previous.setOnClickListener {
                currentMonth = currentMonth.minusMonths(1)
                updateCalendar()

            }
            val next = monthView.findViewById<ImageButton>(R.id.button_next)
            next.setOnClickListener {
                currentMonth = currentMonth.plusMonths(1)
                updateCalendar()
            }
            parentLayout.addView(monthView)
        }
    }

    private fun generateCalendarWeeks(yearMonth: YearMonth): List<List<LocalDate?>> {
        // Generates a list of weeks, each containing dates for the specified month.
        val weeks = ArrayList<List<LocalDate?>>()
        var week = ArrayList<LocalDate?>()

        val firstDayOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            yearMonth.atDay(1).dayOfWeek.value % 7
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Add null values for the days before the start of the month
        for (i in 0 until firstDayOfMonth) {
            week.add(null)
        }

        val daysInMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            yearMonth.lengthOfMonth()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        for (day in 1..daysInMonth) {
            week.add(yearMonth.atDay(day))
            if (week.size == 7) {
                weeks.add(week)
                week = ArrayList()
            }
        }

        // Add null values for the days after the end of the month
        while (week.size < 7) {
            week.add(null)
        }
        if (week.isNotEmpty()) {
            weeks.add(week)
        }
        return weeks
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}