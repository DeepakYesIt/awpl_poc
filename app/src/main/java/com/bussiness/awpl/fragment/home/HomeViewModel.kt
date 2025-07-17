package com.bussiness.awpl.fragment.home

import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.model.AgoraCallModel
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.model.OrganDeptModel
import com.bussiness.awpl.repository.AwplRepository
import com.bussiness.awpl.viewmodel.DiseaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.http.Field
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    var upcomingDate :String =""
    var upcomingTime :String =""
    var startAppoitmentTime :String =""
    var date :String =""

    private val _homeData = MutableLiveData<HomeModel?>()
    val homeData: LiveData<HomeModel?> = _homeData

    private var timerJob: Job? = null
    val notificationStatus = MutableLiveData<Boolean>()

    suspend fun submitFeedBack(
        appointmentId: Int,
        rating: Int,
        comment: String
    ): Flow<NetworkResult<String>> {
        return repository.submitFeedBack(appointmentId, rating, comment).onEach {

        }
    }


    fun startPeriodicFetch() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val now = Calendar.getInstance()
                val minute = now.get(Calendar.MINUTE)
                val seconds = now.get(Calendar.SECOND)

                // Align to the next 5-min mark
                val delayToNext5Min = ((5 - (minute % 5)) * 60 - seconds).coerceAtLeast(0)
                delay(delayToNext5Min * 1000L)
                fetchHomeData()
                // Then repeat every 5 minutes
                while (isActive) {
                    delay(5 * 60 * 1000L)
                    fetchHomeData()
                }
            }
        }
    }

    private suspend fun fetchHomeData() {
        repository.getHomeData()
            .catch { e ->
                e.printStackTrace()
            }
            .collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Log.d("Inside_API","inside success of View Model")
                        result.data.let{
                            notificationStatus.postValue( it?.notification_present)
                        }
                        _homeData.postValue(result.data)
                    }
                    is NetworkResult.Error -> {
                        // Handle error, show toast/message or update LiveData
                    }
                    is NetworkResult.Loading -> {
                        // Optional: Show loader
                    }
                }
            }
    }

    fun stopPeriodicFetch() {
        timerJob?.cancel()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeMoreThanTwoHoursAhead(dateStr: String, timeRange: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy h:mm a", Locale.ENGLISH)
            val todayYear = LocalDate.now().year

            // Combine start time with date
            val parts = timeRange.split("-")
            if (parts.size != 2) return false

            val startTime = parts[0].trim()       // e.g. 5:15
            val endTime = parts[1].trim()         // e.g. 5:30 PM

            // Append PM/AM from endTime to startTime if needed
            val amPm = endTime.takeLast(2)        // "PM"
            val normalizedStart = "$startTime $amPm"  // "5:15 PM"

            // Combine date and time
            val fullDateTimeStr = "$dateStr $todayYear $normalizedStart" // "Fri Jun 13 2025 5:15 PM"

            val inputDateTime = LocalDateTime.parse(fullDateTimeStr, formatter)
            val now = LocalDateTime.now()

            // Return true if it's more than 2 hours ahead
            Duration.between(now, inputDateTime).toMinutes() > 120

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    val organList = listOf(
        OrganDeptModel(R.drawable.cadiologist, "Heart"),
        OrganDeptModel(R.drawable.dental, "Dental"),
        OrganDeptModel(R.drawable.kidney, "Kidney"),
        OrganDeptModel(R.drawable.gastroenterologists, "Stomach"),
        OrganDeptModel(R.drawable.dental, "Dental") ,
         OrganDeptModel(R.drawable.ic_tb, "Tuberclosis(TB)"),
         OrganDeptModel(R.drawable.ic_allergy, "Allergy"),
        OrganDeptModel(R.drawable.ic_blood_pressure, "Blood\nPressure"),
        OrganDeptModel(R.drawable.ic_typhoid, "Typhoid"),
        OrganDeptModel(R.drawable.ic_liver, "Liver"),
        OrganDeptModel(R.drawable.ic_thyroid, "Thyroid"),
        OrganDeptModel(R.drawable.ic_gyno, "Gynic\nDisease"),
    )

//    val healthJourneyList = listOf(
//        HealthJourneyItem("Begin Your Health\n Journey with a \nFree Consultation!", R.drawable.women_doctor),
//        HealthJourneyItem("Bringing Doctors\n to Your Door – \nVirtually.", R.drawable.women_doctor),
//        HealthJourneyItem("Upload Symptoms \nfor Minor Issues \nand Major Concerns", R.drawable.women_doctor)
//    )

//    val browseVideoList = listOf(
//        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
//        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
//        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
//        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
//        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
//        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail)
//    )

    suspend fun getDiseaseList(): Flow<NetworkResult<MutableList<DiseaseModel>>> {
        return repository.diseaseList().onEach {

        }
    }

    suspend fun getHomeData(): Flow<NetworkResult<HomeModel>>{
        return repository.getHomeData().onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    Log.d("Inside_API","inside success of View Model")
                    result.data.let{
                        notificationStatus.postValue( it?.notification_present)
                    }
                    _homeData.postValue(result.data)
                }
                is NetworkResult.Error -> {
                    // Handle error, show toast/message or update LiveData
                }
                is NetworkResult.Loading -> {
                    // Optional: Show loader
                }
            }
        }


    }

    suspend fun createChannel(appointmentId: Int): Flow<NetworkResult<AgoraCallModel>>{
        return repository.createChannel(appointmentId).onEach {

        }
    }


    suspend fun callJoined(@Field("appointmentId") appointmentId: Int) :Flow<NetworkResult<String>>{
        return repository.callJoined(appointmentId).onEach {

        }
    }


    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> get() = _timeLeft

    private var countDownTimer: CountDownTimer? = null
    private var timeSetMillis: Long = 0

    fun setTimerTime(durationMillis: Long) {
        timeSetMillis = durationMillis
        startTimerIfNotRunning()
    }

    private fun startTimerIfNotRunning() {
        if (countDownTimer != null) return

        countDownTimer = object : CountDownTimer(timeSetMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished / 1000
                timeSetMillis = millisUntilFinished // persist remaining time
            }

            override fun onFinish() {
                _timeLeft.value = 0
                countDownTimer = null
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }


}
