package com.bussiness.awpl

import android.app.Application
import com.bussiness.awpl.utils.SessionManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val sessionManager = SessionManager(applicationContext)
        sessionManager.applySavedLanguage()
        FirebaseApp.initializeApp(this)

        AppContextProvider.initialize(applicationContext)
    }



}