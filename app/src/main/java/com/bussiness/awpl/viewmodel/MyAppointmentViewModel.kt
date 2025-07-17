package com.bussiness.awpl.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.AgoraCallModel
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.model.CompletedScheduleCallModel
import com.bussiness.awpl.model.CompletedSymptomsModel
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.model.IncompleteAppoint
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.repository.AwplRepository
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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MyAppointmentViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {


     var isSelectedUploadSymptoms = false
    var upcomingList = mutableListOf<UpcomingModel>()
    private var cancelList = mutableListOf<CancelledAppointment>()
    private var incompleteAppointmentList = mutableListOf<IncompleteAppoint>()
    private val _homeData = MutableLiveData<MutableList<UpcomingModel>?>()

    val homeData: LiveData<MutableList<UpcomingModel>?> = _homeData

    private var timerJob: Job? = null


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
        repository.upcomingAppointment()
            .catch { e ->
                e.printStackTrace()
            }
            .collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Log.d("Inside_API","inside success of View Model")
                        result.data?.let {
                            if(result.data != null) {
                                _homeData.postValue(result.data)
                            }
                        }
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



    suspend fun upcomingAppoint() : Flow<NetworkResult<MutableList<UpcomingModel>>> {
        return repository.upcomingAppointment().onEach{
            when(it){
                is NetworkResult.Success ->{
                    it.data?.let {
                        upcomingList.addAll(it)
                    }
                }
                is NetworkResult.Error ->{

                }
                else ->{

                }
            }
        }
    }

    suspend fun createChannel(appointmentId: Int): Flow<NetworkResult<AgoraCallModel>>{
        return repository.createChannel(appointmentId).onEach {

        }
    }
    suspend fun callJoined(
        @Field("appointmentId") appointmentId: Int
    ) :Flow<NetworkResult<String>>{
       return repository.callJoined(appointmentId).onEach {

        }
    }


    suspend fun completedAppointment(appointmentFor: String):  Flow<NetworkResult<MutableList<CompletedScheduleCallModel>>> {
        return repository.completedAppointment(appointmentFor).onEach {

        }
    }

    suspend fun cancelAppointment(): Flow<NetworkResult<MutableList<CancelledAppointment>>> {
        return repository.cancelAppointment().onEach {
            it.data?.let {
                cancelList.addAll(it)
            }
        }
    }

    suspend fun completedSymptomsUpload() :  Flow<NetworkResult<MutableList<CompletedSymptomsModel>>>{
        return repository.completedSymptomsUpload().onEach {

        }
    }

    suspend fun incompleteAppointment(): Flow<NetworkResult<MutableList<IncompleteAppoint>>> {
        return repository.incompleteAppointment().onEach {
            it.data?.let {
                incompleteAppointmentList.addAll(it)
            }
        }
    }

}