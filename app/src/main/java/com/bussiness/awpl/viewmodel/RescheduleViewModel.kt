package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class RescheduleViewModel  @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun resheduleAppointment(
        @Field("appointment_id") appointmentId : Int,
        @Field("date") date :String,
        @Field("time") time :String
    ) : Flow<NetworkResult<String>>{
        return repository.resheduleAppointment(appointmentId, date, time).onEach {

        }
    }

}