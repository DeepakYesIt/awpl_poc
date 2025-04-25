package com.business.zyvo.remote


import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST

import retrofit2.http.PUT

import retrofit2.http.Part
import retrofit2.http.Path
import java.nio.file.DirectoryStream.Filter


interface ZyvoApi {

    @POST("login")
    @FormUrlEncoded
    suspend fun login(@Field("ds_code")dsCode :String,@Field("password")password :String) :Response<JsonObject>




}