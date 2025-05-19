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


    @POST("basicInfo")
    @FormUrlEncoded
    suspend fun basicInfo(@Field("name") name :String,
                          @Field("height") height :String,
                          @Field("weight")weight :String,
                          @Field("age") age:String,
                          @Field("gender") gender :String
                          ) : Response<JsonObject>

    @POST("termsCondition")
    suspend fun termsCondition() : Response<JsonObject>

    @POST("refundPolicy")
    suspend fun refundPolicy() : Response<JsonObject>

    @POST("privacyPolicy")
    suspend fun privacyPolicy() : Response<JsonObject>

    @POST("faq")
    suspend fun faq() : Response<JsonObject>

    @POST("videoGallery")
    suspend fun videoGallery() : Response<JsonObject>

    @POST("yourDoctor")
    suspend fun doctor() : Response<JsonObject>

    @POST("myProfile")
    suspend fun getMyProfile() : Response<JsonObject>

    @POST("updateProfile")
    @Multipart
    suspend fun updateProfile(
        @Part("name")name :RequestBody,
        @Part("height")height :RequestBody,
        @Part ("weight") weight : RequestBody,
        @Part ("age") age : RequestBody,
        @Part ("gender") gender : RequestBody,
        @Part profileImage: MultipartBody.Part?
        ) : Response<JsonObject>


    @POST("disease")
    suspend fun diseaseList() :Response<JsonObject>

    @POST("home")
    suspend fun getHomeData() : Response<JsonObject>

    @POST("symptomUpload")
    @Multipart
    suspend fun symptomsUpload(
        @Part("answer1") answer1 : RequestBody,
        @Part("answer2") answer2 : RequestBody,
        @Part("answer3") answer3 : RequestBody,
        @Part("answer4") answer4 : RequestBody,
        @Part profileImageList : ArrayList<MultipartBody.Part>?,
        @Part videoList : ArrayList<MultipartBody.Part>?,
        @Part pdfList : ArrayList<MultipartBody.Part>?,
        @Part("disease") disease : RequestBody
    ) : Response<JsonObject>


    @POST("scheduledCallForMe")
    @Multipart
    suspend fun scheduleCallForMe(
        @Part("answer1") answer1 : RequestBody,
        @Part("answer2") answer2 : RequestBody,
        @Part("answer3") answer3 : RequestBody,
        @Part("answer4") answer4 : RequestBody,
        @Part("disease") disease : RequestBody,
        @Part profileImageList : ArrayList<MultipartBody.Part>?
    ) : Response<JsonObject>

    @POST("scheduledCallForOthers")
    @Multipart
    suspend fun scheduleCallForOther(
        @Part("answer1") answer1 : RequestBody,
        @Part("answer2") answer2 : RequestBody,
        @Part("answer3") answer3 : RequestBody,
        @Part("answer4") answer4 : RequestBody,
        @Part("disease") disease : RequestBody,
        @Part profileImageList : ArrayList<MultipartBody.Part>?,
        @Part("name") name : RequestBody,
        @Part("age") age :RequestBody,
        @Part("height") height : RequestBody,
        @Part("weight") weight :RequestBody,
        @Part("gender") gender :RequestBody
    ) : Response<JsonObject>


    @POST("bookAppointment")
    @FormUrlEncoded
    suspend fun bookingAppointment(
        @Field("date") date: String,
        @Field("time") time :String,
        @Field("schedule_call_id") callId :Int
    ) : Response<JsonObject>

    @POST("upcomingAppointment")
    suspend fun upcomingAppointment() : Response<JsonObject>


    @POST("completedAppointment")
    @FormUrlEncoded
    suspend fun completeAppoitment(
        @Field("for")appointmentFor:String
    ) : Response<JsonObject>

    @POST("getScheduleTime")
    @FormUrlEncoded
    suspend fun getScheduleTime(@Field("date")date:String) : Response<JsonObject>

    @POST("applyPromo")
    @FormUrlEncoded
    suspend fun applyPromoCode(
        @Field("promoCode") promoCode :String,
        @Field("appointment_id") appointmentId :Int
    ) : Response<JsonObject>

    @POST("cancelledAppointment")
    suspend fun cancelAppointment() : Response<JsonObject>


}