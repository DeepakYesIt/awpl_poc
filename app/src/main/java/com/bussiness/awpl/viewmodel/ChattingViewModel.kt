package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.ChatAppotmentDetails
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel  @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun checkAppoitmentDetails(appointmentId: Int)
    : Flow<NetworkResult<ChatAppotmentDetails>>{
          return repository.checkAppoitmentDetails(appointmentId).onEach {

          }
    }

    suspend fun saveChat(
        appointmentId: Int,
        message: String
    ): Flow<NetworkResult<String>> {
        return repository.saveChat(appointmentId,message).onEach {

        }
    }


}