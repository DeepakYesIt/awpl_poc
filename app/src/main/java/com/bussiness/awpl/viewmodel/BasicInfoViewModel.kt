package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BasicInfoViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

     suspend fun basicInfo(
        name: String, height: String, weight: String, age: String, gender: String,state :String): Flow<NetworkResult<String>>{
        return repository.basicInfo(name, height, weight, age, gender,state).onEach {

        }
    }

}