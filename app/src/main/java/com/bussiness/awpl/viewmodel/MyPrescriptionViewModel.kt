package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.PrescriptionModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyPrescriptionViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun myPrescription(forWhich:String): Flow<NetworkResult<MutableList<PrescriptionModel>>>{
        return repository.myPrescription(forWhich).onEach {

        }
    }
}