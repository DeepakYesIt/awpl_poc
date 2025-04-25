package com.bussiness.awpl.utils

import android.app.Application
import android.content.Context
import com.bussiness.awpl.utils.SessionManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize SessionManager and apply saved language
        val sessionManager = SessionManager(applicationContext)
        sessionManager.applySavedLanguage()
    }
}