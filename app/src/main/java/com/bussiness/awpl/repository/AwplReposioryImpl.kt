package com.bussiness.awpl.repository

import android.util.Log
import com.business.zyvo.remote.ZyvoApi
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.DoctorModel
import com.bussiness.awpl.model.ErrorHandler
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.model.HomeModel
import com.bussiness.awpl.model.LoginModel
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
import retrofit2.http.Part
import java.io.IOException
import javax.inject.Inject

class AwplReposioryImpl  @Inject constructor(private val api: ZyvoApi) : AwplRepository{

    var TAG = "AWPL_RESP_IMPL";

    override suspend fun login(dsCode: String, password: String): Flow<NetworkResult<LoginModel>> = flow{

        try {
            api.login(dsCode, password).apply {
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

}