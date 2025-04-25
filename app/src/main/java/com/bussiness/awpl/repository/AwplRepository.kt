package com.bussiness.awpl.repository

import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.LoginModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Field

import kotlinx.coroutines.flow.Flow

interface AwplRepository {


    suspend fun login(dsCode :String, password :String) : Flow<NetworkResult<LoginModel>>



}