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
class ScheduleCallViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun scheduleCallForMe(answer1 : RequestBody,
                                  answer2 : RequestBody,
                                  answer3 : RequestBody,
                                  answer4 : RequestBody,
                                  disease : RequestBody,
                                  profileImageList : ArrayList<MultipartBody.Part>?) : Flow<NetworkResult<String>> {
        return repository.scheduleCallForMe(answer1, answer2, answer3, answer4, disease, profileImageList).onEach{

        }
    }

    

}