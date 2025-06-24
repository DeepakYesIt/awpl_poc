package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.PatinetNotification
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun patientNotification(): Flow<NetworkResult<MutableList<PatinetNotification>>>{
        return repository.patientNotification().onEach {
        }
    }

    suspend fun markAllRead(): Flow<NetworkResult<String>>{
        return repository.markAllRead().onEach {
        }
    }

    suspend fun markNotificationRead(id:String):Flow<NetworkResult<String>>{
        return repository.markAsReadPatientNotification(id).onEach {

        }
    }



}