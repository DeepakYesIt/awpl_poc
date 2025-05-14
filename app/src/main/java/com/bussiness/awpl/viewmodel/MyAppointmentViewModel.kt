package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyAppointmentViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun upcomingAppoint() : Flow<NetworkResult<MutableList<UpcomingModel>>> {
        return repository.upcomingAppointment().onEach{

        }
    }


    suspend fun completedAppointment(appointmentFor: String): Flow<NetworkResult<MutableList<CompletedAppointmentModel>>> {
        return repository.completedAppointment(appointmentFor).onEach {

        }
    }


}