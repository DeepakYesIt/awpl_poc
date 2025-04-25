package com.bussiness.awpl.repository

import android.util.Log
import com.business.zyvo.remote.ZyvoApi
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.LoginModel
import com.bussiness.awpl.utils.AppConstant
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AwplReposioryImpl  @Inject constructor(private val api: ZyvoApi) : AwplRepository{

    var TAG = "AWPL_RESP_IMPL";

    override suspend fun login(dsCode: String, password: String): Flow<NetworkResult<LoginModel>> = flow{

        try {
            api.login(dsCode, password).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
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
        } catch (e: HttpException) {
            Log.e(TAG, "http exception - ${e.message}")
            emit(NetworkResult.Error(e.message!!))
        } catch (e: IOException) {
            Log.e(TAG, "io exception - ${e.message} :: ${e.localizedMessage}")
            emit(NetworkResult.Error(e.message!!))
        } catch (e: Exception) {
            Log.e(TAG, "exception - ${e.message} :: \n ${e.stackTraceToString()}")
            emit(NetworkResult.Error(e.message!!))
        }
    }

}