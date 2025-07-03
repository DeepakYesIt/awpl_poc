package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun myProfile() : Flow<NetworkResult<MyprofileModel>> {
        return repository.getMyProfile().onEach{

        }
    }

     suspend fun updateProfile(
        name : RequestBody,
        height : RequestBody,
        weight : RequestBody,
        age : RequestBody,
        gender : RequestBody,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<String>>{
        return repository.updateProfile(name, height, weight, age, gender, profileImage).onEach {

        }
    }


    suspend fun deleteAccount(): Flow<NetworkResult<String>>{
        return repository.deleteAccount().onEach {

        }
    }


}