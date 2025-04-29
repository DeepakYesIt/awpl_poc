package com.bussiness.awpl

import android.app.Application
import com.bussiness.awpl.utils.SessionManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val sessionManager = SessionManager(applicationContext)
        sessionManager.applySavedLanguage()
    }



}