package com.bussiness.awpl.fragment.home

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.model.AgoraCallModel
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.model.OrganDeptModel
import com.bussiness.awpl.repository.AwplRepository
import com.bussiness.awpl.viewmodel.DiseaseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

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
        return repository.getHomeData().onEach {

        }

    }
    suspend fun createChannel(appointmentId: Int): Flow<NetworkResult<AgoraCallModel>>{
        return repository.createChannel(appointmentId).onEach {

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
