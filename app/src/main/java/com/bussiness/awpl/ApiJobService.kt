package com.bussiness.awpl

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.JobIntentService
import kotlinx.coroutines.runBlocking
//
//class ApiJobService : JobIntentService() {
////    override fun onHandleWork(intent: Intent) {
////        runBlocking {
////            try {
////                val response = RetrofitClient.apiService.getData()
////                Log.d("ApiJobService", "API success: ${response.message}")
////                saveData(applicationContext, response.message)
////            } catch (e: Exception) {
////                Log.e("ApiJobService", "API failed: ${e.message}")
////            }
////        }
////        ApiScheduler.scheduleNextExact5Minute(applicationContext)
////    }
////
////    companion object {
////        fun enqueueWork(context: Context) {
////            enqueueWork(context, ApiJobService::class.java, 1234, Intent(context, ApiJobService::class.java))
////        }
////    }
////
////    private fun saveData(context: Context, data: String) {
////        PreferenceManager.getDefaultSharedPreferences(context)
////            .edit().putString("api_data", data).apply()
////    }
//}