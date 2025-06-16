package com.bussiness.awpl.repository

import android.util.Log
import com.business.zyvo.remote.ZyvoApi
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.AgoraCallModel
import com.bussiness.awpl.model.AppointmentModel
import com.bussiness.awpl.model.BookingResponseModel
import com.bussiness.awpl.model.CancelledAppointment
import com.bussiness.awpl.model.ChatAppotmentDetails
import com.bussiness.awpl.model.CompletedAppointmentModel
import com.bussiness.awpl.model.CompletedScheduleCallModel
import com.bussiness.awpl.model.CompletedSymptomsModel
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.ErrorHandler
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.model.PatinetNotification
import com.bussiness.awpl.model.PayuPaymentModel
import com.bussiness.awpl.model.PrescriptionModel
import com.bussiness.awpl.model.PromoCodeModel
import com.bussiness.awpl.model.ScheduleTimeModel
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.model.VideoModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.viewmodel.DiseaseModel
import com.bussiness.awpl.viewmodel.MyprofileModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.Part
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AwplReposioryImpl  @Inject constructor(private val api: ZyvoApi) : AwplRepository{
    var TAG = "AWPL_RESP_IMPL";

    override suspend fun login(dsCode: String, password: String,fcmToken :String, type:String): Flow<NetworkResult<LoginModel>> = flow{

        try {
            api.login(dsCode, password,fcmToken,type).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            val model: LoginModel =
                                Gson().fromJson(obj.toString(), LoginModel::class.java)
                            emit(NetworkResult.Success<LoginModel>(model))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun basicInfo(
        name: String, height: String, weight: String, age: String, gender: String): Flow<NetworkResult<String>> = flow {

            try {
                api.basicInfo(name, height, weight, age, gender).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                           var obj = resp.get("message").asString
                            emit(NetworkResult.Success<String>(obj))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }

    }

    override suspend fun termsCondition(): Flow<NetworkResult<String>> = flow {
        try {
            api.termsCondition().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var termCondition = obj.get("termsCondition").asString
                            emit(NetworkResult.Success<String>(termCondition))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun refundPolicy(): Flow<NetworkResult<String>> =flow{
        try {
            api.refundPolicy().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var refund = obj.get("refundPolicy").asString
                            emit(NetworkResult.Success<String>(refund))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun privacyPolicy(): Flow<NetworkResult<String>> =flow{
        try {
            api.privacyPolicy().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var privacyPolicy = obj.get("privacyPolicy").asString
                            emit(NetworkResult.Success<String>(privacyPolicy))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun faq(): Flow<NetworkResult<MutableList<FAQItem>>> = flow {
        try {
            api.faq().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var faqData = obj.get("faqDetails").asJsonArray
                            var resultList = mutableListOf<FAQItem>()
                            faqData.forEach {
                                val model: FAQItem =
                                    Gson().fromJson(it.toString(), FAQItem::class.java)
                                 resultList.add(model)
                            }
                            emit(NetworkResult.Success(resultList))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let {
                            JSONObject(it)
                        }
                        emit(NetworkResult.Error(jsonObj?.getString("message") ?: AppConstant.unKnownError))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun videoGallery(): Flow<NetworkResult<MutableList<VideoModel>>> = flow {
        try {
            api.videoGallery().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var faqData = obj.get("videos").asJsonArray
                            var resultList = mutableListOf<VideoModel>()
                            faqData.forEach {
                                val model: VideoModel =
                                    Gson().fromJson(it.toString(), VideoModel::class.java)
                                resultList.add(model)
                            }
                            Log.d("TESTING_URL","Result List Size is :- "+resultList.size)
                            emit(NetworkResult.Success(resultList))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun scheduleCallForMe(
        answer1: RequestBody,
        answer2: RequestBody,
        answer3: RequestBody,
        answer4: RequestBody,
        disease: RequestBody,
        profileImageList: ArrayList<MultipartBody.Part>?
    ): Flow<NetworkResult<String>> =flow {
        try {
            api.scheduleCallForMe(answer1, answer2, answer3, answer4, disease, profileImageList).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {

                           var data = resp.get("data").asJsonArray

                            var id =-1


                            data.forEach {
                                var obj1 = it.asJsonObject
                                 id = obj1.get("id").asInt
                            }
                            emit(NetworkResult.Success(id.toString()))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(NetworkResult.Error(jsonObj?.getString("message") ?: AppConstant.unKnownError))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }

    }

    override suspend fun scheduleCallForOther(
        answer1: RequestBody,
        answer2: RequestBody,
        answer3: RequestBody,
        answer4: RequestBody,
        disease: RequestBody,
        profileImageList: ArrayList<MultipartBody.Part>?,
        name: RequestBody,
        age: RequestBody,
        height: RequestBody,
        weight: RequestBody,
        gender: RequestBody
    ): Flow<NetworkResult<String>> =flow{
        try {
            api.scheduleCallForOther(answer1, answer2, answer3, answer4, disease, profileImageList, name, age, height, weight, gender).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var data = resp.get("data").asJsonArray
                            var id =-1
                            data.forEach {
                                var obj1 = it.asJsonObject
                                id = obj1.get("id").asInt
                            }
                            emit(NetworkResult.Success(id.toString()))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }

        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun bookingAppointment(
        date: String,
        time: String,
        callId: Int
    ): Flow<NetworkResult<BookingResponseModel>> =flow{
        try {
            api.bookingAppointment(date, time, callId).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            val obj = resp.get("data").asJsonObject
                            val model: BookingResponseModel = Gson().fromJson(obj.toString(), BookingResponseModel::class.java)
                            emit(NetworkResult.Success(model))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(NetworkResult.Error(jsonObj?.getString("message") ?: AppConstant.unKnownError))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    fun isTimeRangeInPast(dateStr: String, timeRangeStr: String): Boolean {
        return try {
            Log.d("TESTING_TIME", "$dateStr $timeRangeStr")

            val dateFormatter = SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH)
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val fullDateStr = "$dateStr $year"

            val endTimeStr = timeRangeStr.split("-")[1].trim() // e.g., "01:30 PM"
            val fullDateTimeStr = "$fullDateStr $endTimeStr"   // e.g., "Mon Jun 16 2025 01:30 PM"

            val scheduledEndTime = dateFormatter.parse(fullDateTimeStr)
            val now = Date()

            scheduledEndTime?.before(now) ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    override suspend fun upcomingAppointment(): Flow<NetworkResult<MutableList<UpcomingModel>>> = flow{
        try {
            api.upcomingAppointment().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var appoitmentArr= obj.get("appointmentDetails").asJsonArray
                            var result = mutableListOf<UpcomingModel>()
                            appoitmentArr.forEach {
                             val model: UpcomingModel = Gson().fromJson(it.toString(), UpcomingModel::class.java)
                             if(!isTimeRangeInPast(model.date,model.time)) {
                                  result.add(model)
                             }
                        }
                            emit(NetworkResult.Success(result))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun completedAppointment(appointmentFor: String): Flow<NetworkResult<MutableList<CompletedScheduleCallModel>>> =flow{
        try {
            api.completeAppoitment(appointmentFor).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject

                            var appoitmentArr= obj.get("appointmentDetails").asJsonArray
                            var result = mutableListOf<CompletedScheduleCallModel>()
                            appoitmentArr.forEach {
                                val model: CompletedScheduleCallModel = Gson().fromJson(it.toString(), CompletedScheduleCallModel::class.java)
                                result.add(model)
                            }

                            emit(NetworkResult.Success(result))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun getScheduleTime(date :String): Flow<NetworkResult<ScheduleTimeModel>> = flow {
            try {
            api.getScheduleTime(date).apply {
                if (isSuccessful) {
                    body()?.let {
                        resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var timeSlotArr = obj.get("available_time_slots").asJsonArray
                            var resultList = mutableListOf<String>()
                            var scheduleModel = ScheduleTimeModel()
                            var profileObj = obj.get("profile_details").asJsonObject

                            if(profileObj.has("email") && profileObj.get("email").isJsonNull ==false){
                               scheduleModel.email = profileObj.get("email").asString
                            }

                            if(profileObj.has("phone") && profileObj.get("phone").isJsonNull == false){
                                scheduleModel.phone = profileObj.get("phone").asString
                            }


                            timeSlotArr.forEach {
                               var obj = it.asJsonObject
                                var availableTime = obj.get("is_available").asBoolean
                                if(availableTime){
                                    resultList.add(obj.get("display_time").asString)
                                }

                            }



                            scheduleModel.timeSlotList = resultList


                            Log.d("TESTING_URL","Result List Size is :- "+resultList.size)
                            emit(NetworkResult.Success(scheduleModel))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                }
                else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    }
                    catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }

    }

    override suspend fun applyPromoCode(
        promoCode: String,
        appointmentId: Int
    ): Flow<NetworkResult<PromoCodeModel>> =flow{
        try {
            api.applyPromoCode(promoCode, appointmentId).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            val model: PromoCodeModel =
                                    Gson().fromJson(obj.toString(), PromoCodeModel::class.java)



                            emit(NetworkResult.Success(model))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                              NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun cancelAppointment(): Flow<NetworkResult<MutableList<CancelledAppointment>>> =flow{
        try {
            api.cancelAppointment().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var cancelAppoitment = obj.get("appointmentDetails").asJsonArray
                            var resultList = mutableListOf<CancelledAppointment>()
                            cancelAppoitment.forEach {
                                val model: CancelledAppointment =
                                    Gson().fromJson(it.toString(), CancelledAppointment::class.java)
                                resultList.add(model)
                            }

                            Log.d("TESTING_URL","Result List Size is :- "+resultList.size)
                            emit(NetworkResult.Success(resultList))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun patientNotification(): Flow<NetworkResult<MutableList<PatinetNotification>>> =flow{
        try {
            Log.d("TESTING_NOTIFICATION","inside notification Api")
            api.patientNotification().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonArray

                            var resultList = mutableListOf<PatinetNotification>()
                            obj.forEach {
                                val model: PatinetNotification = Gson().fromJson(it.toString(), PatinetNotification::class.java)
                                resultList.add(model)
                            }

                            Log.d("TESTING_URL","Result List Size is :- "+resultList.size)
                            emit(NetworkResult.Success(resultList))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun markAllRead(): Flow<NetworkResult<String>> =flow{
        try {
            Log.d("TESTING_NOTIFICATION","inside notification Api")
            api.markAllRead().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("message").asString


                            emit(NetworkResult.Success(obj))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }


    override suspend fun doctor(): Flow<NetworkResult<MutableList<DoctorModel>>> = flow {
        try {
            api.doctor().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var availableDoctor = obj.get("availableDoctors").asJsonArray
                            var resultList = mutableListOf<DoctorModel>()
                            availableDoctor.forEach {
                                val model: DoctorModel =
                                    Gson().fromJson(it.toString(), DoctorModel::class.java)
                                resultList.add(model)
                            }

                            Log.d("TESTING_URL","Result List Size is :- "+resultList.size)
                            emit(NetworkResult.Success(resultList))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun getMyProfile() : Flow<NetworkResult<MyprofileModel>> = flow{
        try {
            api.getMyProfile().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            val obj = resp.get("data").asJsonObject
                            val model: MyprofileModel = Gson().fromJson(obj.toString(), MyprofileModel::class.java)
                            emit(NetworkResult.Success(model))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun updateProfile(
        name : RequestBody,
        height : RequestBody,
        weight : RequestBody,
        age : RequestBody,
        gender : RequestBody,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<String>> =flow{
        try {
            api.updateProfile(name, height, weight, age, gender, profileImage).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            val obj = resp.get("message").asString
                            var mainObj = resp.get("data").asJsonObject
                            var obj1 = mainObj.get("patient").asJsonObject
                            var img = ""
                            if(obj1.has("profile_path") && obj1.get("profile_path").isJsonNull == false){
                                 img = AppConstant.Base_URL + obj1.get("profile_path").asString
                            }


                            emit(NetworkResult.Success(obj + "-----"+img))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun diseaseList(): Flow<NetworkResult<MutableList<DiseaseModel>>> =flow{
        try {
            api.diseaseList().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            val data = resp.get("diseases").asJsonArray
                            var result = mutableListOf<DiseaseModel>()

                            data.forEach {
                                val model: DiseaseModel = Gson().fromJson(it.toString(), DiseaseModel::class.java)
                                result.add(model)
                            }

                            emit(NetworkResult.Success(result))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun getHomeData(): Flow<NetworkResult<HomeModel>> = flow{
        try {
            api.getHomeData().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            val model: HomeModel = Gson().fromJson(obj.toString(), HomeModel::class.java)
                            emit(NetworkResult.Success(model))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun symptomsUpload(
        answer1: RequestBody,
        answer2: RequestBody,
        answer3: RequestBody,
        answer4: RequestBody,
        profileImageList: ArrayList<MultipartBody.Part>?,
        videoList: ArrayList<MultipartBody.Part>?,
        pdfList: ArrayList<MultipartBody.Part>?,
        disease : RequestBody
    ): Flow<NetworkResult<String>> =flow{
        try {
            api.symptomsUpload(answer1, answer2, answer3, answer4, profileImageList, videoList, pdfList,disease).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("message").asString
                            emit(NetworkResult.Success(obj))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

   override  suspend fun resheduleAppointment(
        @Field("appointment_id") appointmentId : Int,
        @Field("date") date :String,
        @Field("time") time :String
    ) : Flow<NetworkResult<String>> = flow{
        try {
            api.resheduleAppointment(appointmentId, date, time).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("message").asString
                            emit(NetworkResult.Success(obj))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun completedSymptomsUpload(): Flow<NetworkResult<MutableList<CompletedSymptomsModel>>> =flow{
        try {
            api.completedSymptomsUpload().apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var completeSymptomsUpload = obj.get("completed_symptom_uploads").asJsonArray
                            var resultList = mutableListOf<CompletedSymptomsModel>()
                            completeSymptomsUpload.forEach {
                                val model: CompletedSymptomsModel =
                                    Gson().fromJson(it.toString(), CompletedSymptomsModel::class.java)
                                 resultList.add(model)
                            }


                            emit(NetworkResult.Success(resultList))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun myPrescription(forWhich :String): Flow<NetworkResult<MutableList<PrescriptionModel>>> =flow{
//        try {
            api.myPrescription(forWhich).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var completeSymptomsUpload = obj.get("prescriptions").asJsonArray
                            var resultList = mutableListOf<PrescriptionModel>()
                            completeSymptomsUpload.forEach {
                                val model: PrescriptionModel =
                                    Gson().fromJson(it.toString(), PrescriptionModel::class.java)
                                resultList.add(model)
                            }



                            emit(NetworkResult.Success(resultList))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
//        } catch (e: Exception) {
//            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
//        }
    }

    override suspend fun initiatePayment(
        appointmentId: Int,
        txnId: String,
        amount: String,
        productInfo: String,
        firstName: String?,
        email: String?,
        phone: String?
    ): Flow<NetworkResult<PayuPaymentModel>> =flow{
        try {
            api.initiatePayment(appointmentId, txnId, amount, productInfo, firstName, email, phone).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {

                            var obj = resp.get("data").asJsonObject
                                val model: PayuPaymentModel =
                                    Gson().fromJson(obj.toString(), PayuPaymentModel::class.java)




                            emit(NetworkResult.Success(model))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun createChannel(appointmentId: Int): Flow<NetworkResult<AgoraCallModel>> = flow {

        try {
            api.createChannel(appointmentId).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {

                            var obj = resp.get("data").asJsonObject
                            val model: AgoraCallModel =
                                Gson().fromJson(obj.toString(), AgoraCallModel::class.java)




                            emit(NetworkResult.Success(model))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun checkAppoitmentDetails(
        appointmentId: Int
    ) : Flow<NetworkResult<ChatAppotmentDetails>> = flow {
        try {
        Log.d("TESTING_CHAT_API", appointmentId.toString())
        api.checkAppoitmentDetails(appointmentId).apply {
            if (isSuccessful) {
                body()?.let { resp ->
                    Log.d("TESTING_CHAT_API", "Inside response sucess")
                    if (resp.has("status") && resp.get("status").asBoolean) {
                        Log.d("TESTING_CHAT_API", "Inside response sucess inner")
                        val obj = resp.get("data").asJsonObject
                        val model: ChatAppotmentDetails =
                            Gson().fromJson(obj.toString(), ChatAppotmentDetails::class.java)
                        emit(NetworkResult.Success(model))
                    } else {
                        emit(NetworkResult.Error(resp.get("message").asString))
                    }
                } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
            } else {
                Log.d("TESTING_CHAT_API", "Inside response error inner")
                try {
                    val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                    emit(
                        NetworkResult.Error(
                            jsonObj?.getString("message") ?: AppConstant.unKnownError
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                    emit(NetworkResult.Error(AppConstant.unKnownError))
                }
            }
        }

        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

    override suspend fun callJoined(
        @Field("appointmentId") appointmentId: Int
    ) :Flow<NetworkResult<String>> = flow{
        try {
            api.callJoined(appointmentId).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            val obj = resp.get("message").asString
                          //  val model: ChatAppotmentDetails = Gson().fromJson(obj.toString(), ChatAppotmentDetails::class.java)
                            emit(NetworkResult.Success(obj))
                        }
                        else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    try {
                        val jsonObj = this.errorBody()?.string()?.let { JSONObject(it) }
                        emit(
                            NetworkResult.Error(
                                jsonObj?.getString("message") ?: AppConstant.unKnownError
                            )
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emit(NetworkResult.Error(AppConstant.unKnownError))
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(ErrorHandler.emitError(e)))
        }
    }

}