package com.bussiness.awpl.repository

import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.VideoModel
import com.bussiness.awpl.viewmodel.MyprofileModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Field

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

interface AwplRepository {


    suspend fun login(dsCode :String, password :String) : Flow<NetworkResult<LoginModel>>

    suspend fun basicInfo(@Field("name") name :String,
                          @Field("height") height :String,
                          @Field("weight")weight :String,
                          @Field("age") age:String,
                          @Field("gender") gender :String
    ) : Flow<NetworkResult<String>>

    suspend fun termsCondition() : Flow<NetworkResult<String>>

    suspend fun refundPolicy() : Flow<NetworkResult<String>>

    suspend fun privacyPolicy() : Flow<NetworkResult<String>>

    suspend fun faq() : Flow<NetworkResult<MutableList<FAQItem>>>

    suspend fun videoGallery() : Flow<NetworkResult<MutableList<VideoModel>>>

    suspend fun doctor() : Flow<NetworkResult<MutableList<DoctorModel>>>

    suspend fun getMyProfile() : Flow<NetworkResult<MyprofileModel>>
    suspend fun updateProfile(
        name : RequestBody,
        height : RequestBody,
       weight : RequestBody,
         age : RequestBody,
       gender : RequestBody,
       profileImage: MultipartBody.Part?
    ) : Flow<NetworkResult<String>>
}