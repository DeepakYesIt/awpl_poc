package com.bussiness.awpl.repository

import android.net.Network
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.AgoraCallModel
import com.bussiness.awpl.model.BookingResponseModel
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.model.ChatAppotmentDetails
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.model.CompletedScheduleCallModel
import com.bussiness.awpl.model.CompletedSymptomsModel
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.HolidayModel
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.model.IncompleteAppoint
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.PatinetNotification
import com.bussiness.awpl.model.PayuPaymentModel
import com.bussiness.awpl.model.PrescriptionModel
import com.bussiness.awpl.model.PromoCodeModel
import com.bussiness.awpl.model.ScheduleTimeModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.model.VideoModel
import com.bussiness.awpl.viewmodel.DiseaseModel
import com.bussiness.awpl.viewmodel.MyprofileModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Field

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

interface AwplRepository {


    suspend fun login(dsCode: String, password: String,fcmToken :String, type:String) : Flow<NetworkResult<LoginModel>>

    suspend fun basicInfo(@Field("name") name :String,
                          @Field("height") height :String,
                          @Field("weight")weight :String,
                          @Field("age") age:String,
                          @Field("gender") gender :String,
                          state:String
    ) : Flow<NetworkResult<String>>

    suspend fun termsCondition() : Flow<NetworkResult<String>>

    suspend fun refundPolicy() : Flow<NetworkResult<String>>

    suspend fun privacyPolicy() : Flow<NetworkResult<String>>

    suspend fun faq() : Flow<NetworkResult<MutableList<FAQItem>>>


    suspend fun doctor() : Flow<NetworkResult<MutableList<DoctorModel>>>

    suspend fun getMyProfile() : Flow<NetworkResult<MyprofileModel>>

    suspend fun updateProfile(
        name : RequestBody,
        height : RequestBody,
        weight : RequestBody,
        age : RequestBody,
        gender : RequestBody,
        profileImage: MultipartBody.Part?,
        state : RequestBody
    ) : Flow<NetworkResult<MyprofileModel>>

    suspend fun diseaseList() : Flow<NetworkResult<MutableList<DiseaseModel>>>

    suspend fun getHomeData() :Flow<NetworkResult<HomeModel>>

    suspend fun symptomsUpload(
         answer1 : RequestBody,
         answer2 : RequestBody,
         answer3 : RequestBody,
         answer4 : RequestBody,
         profileImageList : ArrayList<MultipartBody.Part>?,
         videoList : ArrayList<MultipartBody.Part>?,
         pdfList : ArrayList<MultipartBody.Part>?,
         disease : RequestBody
    ) : Flow<NetworkResult<String>>

    suspend fun videoGallery(): Flow<NetworkResult<MutableList<VideoModel>>>

    suspend fun scheduleCallForMe(
        answer1 : RequestBody,
        answer2 : RequestBody,
        answer3 : RequestBody,
         answer4 : RequestBody,
         disease : RequestBody,
         profileImageList : ArrayList<MultipartBody.Part>?
    ) : Flow<NetworkResult<String>>


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
    ) : Flow<NetworkResult<String>>


    suspend fun bookingAppointment(
        @Field("date") date: String,
        @Field("time") time :String,
        @Field("schedule_call_id") callId :Int
    ) : Flow<NetworkResult<BookingResponseModel>>

    suspend fun upcomingAppointment() : Flow<NetworkResult<MutableList<UpcomingModel>>>

    suspend fun completedAppointment(
        @Field("for")appointmentFor:String
    ) :  Flow<NetworkResult<MutableList<CompletedScheduleCallModel>>>

    suspend fun getScheduleTime(date : String) :Flow<NetworkResult<ScheduleTimeModel>>

    suspend fun applyPromoCode(
         promoCode :String,
         appointmentId :Int
    ) :Flow<NetworkResult<PromoCodeModel>>


    suspend fun cancelAppointment() : Flow<NetworkResult<MutableList<CancelledAppointment>>>

    suspend fun incompleteAppointment() : Flow<NetworkResult<MutableList<IncompleteAppoint>>>

    suspend fun patientNotification() : Flow<NetworkResult<MutableList<PatinetNotification>>>

    suspend fun  markAllRead() : Flow<NetworkResult<String>>

    suspend fun resheduleAppointment(
        @Field("appointment_id") appointmentId : Int,
        @Field("date") date :String,
        @Field("time") time :String
    ) : Flow<NetworkResult<String>>


    suspend fun completedSymptomsUpload() :  Flow<NetworkResult<MutableList<CompletedSymptomsModel>>>


    suspend fun myPrescription(forWhich :String) : Flow<NetworkResult<MutableList<PrescriptionModel>>>


    suspend fun initiatePayment(
        @Field("appointment_id") appointmentId :Int,
        @Field("txnid") txnId :String,
        @Field("amount") amount :String,
        @Field("productinfo") productInfo :String,
        @Field("firstname") firstName :String?,
        @Field("email") email :String?,
        @Field("phone") phone:String?
    ) : Flow<NetworkResult<PayuPaymentModel>>


    suspend fun createChannel(
        @Field("appointmentId") appointmentId :Int
    ) :Flow<NetworkResult<AgoraCallModel>>


    suspend fun checkAppoitmentDetails(
        @Field("appointment_id") appointmentId: Int
    ) : Flow<NetworkResult<ChatAppotmentDetails>>

    suspend fun callJoined(
        @Field("appointmentId") appointmentId: Int
    ) :Flow<NetworkResult<String>>

    suspend fun markAsReadPatientNotification(
        @Field("id") id :String
    ) : Flow<NetworkResult<String>>

    suspend fun saveChat(
        @Field("appointment_id") appointmentId: Int,
        @Field("message") message :String
    ) :Flow<NetworkResult<String>>

    suspend fun deleteAccount() : Flow<NetworkResult<String>>


    suspend fun  submitFeedBack(
        @Field("appointment_id") appointmentId :Int,
        @Field("rating") rating :Int,
        @Field("comment") comment :String
    )  : Flow<NetworkResult<String>>

    suspend fun holidayList() : Flow<NetworkResult<MutableList<HolidayModel>>>
}