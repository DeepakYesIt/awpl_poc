package com.bussiness.awpl.repository

import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.VideoModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Field

import kotlinx.coroutines.flow.Flow

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


}