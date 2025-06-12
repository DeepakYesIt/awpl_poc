package com.bussiness.awpl

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.bussiness.awpl.utils.SessionManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        var currentActivity: Activity? = null
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val sessionManager = SessionManager(applicationContext)
        sessionManager.applySavedLanguage()
        FirebaseApp.initializeApp(this)

        AppContextProvider.initialize(applicationContext)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                if (currentActivity == activity) currentActivity = null
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
    }



