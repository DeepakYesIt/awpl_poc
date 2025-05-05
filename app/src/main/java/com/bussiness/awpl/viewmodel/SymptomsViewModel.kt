package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SymptomsViewModel  @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun symptomsUpload(
        answer1: RequestBody,
        answer2: RequestBody,
        answer3: RequestBody,
        answer4: RequestBody,
        profileImageList: ArrayList<MultipartBody.Part>?,
        videoList: ArrayList<MultipartBody.Part>?,
        pdfList: ArrayList<MultipartBody.Part>?,
        disease : RequestBody
    ): Flow<NetworkResult<String>> {
        return repository.symptomsUpload( answer1,
            answer2, answer3, answer4, profileImageList, videoList, pdfList, disease).onEach{
            }
    }

}