package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class DoctorViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun doctorList() : Flow<NetworkResult<MutableList<DoctorModel>>> {
        return repository.doctor().onEach{

        }
    }

}