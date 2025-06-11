package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.AgoraCallModel
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.model.CompletedScheduleCallModel
import com.bussiness.awpl.model.CompletedSymptomsModel
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class MyAppointmentViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    var upcomingList = mutableListOf<UpcomingModel>()
    private var cancelList = mutableListOf<CancelledAppointment>()


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



}