package com.bussiness.awpl.fragment.bookappointment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.TimeSlotAdapter
import com.bussiness.awpl.databinding.FragmentApointmentBookingBinding
import java.time.LocalDate
import java.time.YearMonth

class AppointmentBooking : Fragment() {

    private var _binding: FragmentApointmentBookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var currentMonth: YearMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        YearMonth.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate? = LocalDate.now()
    private val timeSlots = listOf("09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM", "10:00 AM","10:15 AM", "10:30 AM", "10:45 AM", "11:00 AM")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApointmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        clickListener()
        updateCalendar()
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewTimeSlots.apply {
            this.layoutManager = this@AppointmentBooking.layoutManager
            timeSlotAdapter = TimeSlotAdapter(timeSlots) { selectedTime ->
                binding.textView25.text = "Selected: $selectedTime"
            }
            adapter = timeSlotAdapter
        }
    }


    private fun clickListener() {
        binding.apply {
            leftBtnPrev.setOnClickListener {
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItem > 0) {
                    layoutManager.smoothScrollToPosition(binding.recyclerViewTimeSlots, null, firstVisibleItem - 1)
                }
            }
            rightBtnNext.setOnClickListener {
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem < timeSlots.size - 1) {
                    layoutManager.smoothScrollToPosition(binding.recyclerViewTimeSlots, null, lastVisibleItem + 1)
                }
            }
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.summaryScreen)
            }
            appointmentPolicyTxt.setOnClickListener {
                findNavController().navigate(R.id.appointmentPolicyFragment)
            }
        }
    }

    private fun updateCalendar() {
        // Updates the calendar layout with the current and next month views.
        val calendarLayout = binding.calendarLayout
        calendarLayout.removeAllViews()
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

        addMonthView(calendarLayout, currentMonth)
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
                    // dateView.text = date.dayOfMonth.toString()
                    dateView.text = date.dayOfMonth.toString().padStart(2, '0')

                    dateView.setOnClickListener {
                        selectedDate = date
                        updateCalendar()
                    }

                    when (date) {
                        selectedDate -> {
                            dateView.setBackgroundResource(R.drawable.selected_day_bg)
                            dateView.setTextColor(resources.getColor(R.color.white))
                        }
                        else -> {
                            dateView.setBackgroundResource(R.drawable.date_bg)
                            dateView.setTextColor(
                                if (date.month == yearMonth.month) Color.BLACK else Color.GRAY
                            )
                        }
                    }


//                    dateView.setTextColor(
//                        if (date.month == yearMonth.month) Color.BLACK
//                        else Color.GRAY
//                    )
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
        _binding = null
    }
}
