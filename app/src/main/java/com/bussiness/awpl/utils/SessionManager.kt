package com.bussiness.awpl.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale
import androidx.core.content.edit

class SessionManager(private val context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        const val LANGUAGE_KEY = "language"
        const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    // Save the selected language in SharedPreferences
    fun setLanguage(languageCode: String) {
        preferences.edit {
            putString(LANGUAGE_KEY, languageCode)
        }
    }

    // Get the current selected language from SharedPreferences
    private fun getLanguage(): String {
        return preferences.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    // Change the language dynamically and update the configuration
    fun changeLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the selected language preference
        setLanguage(languageCode)
    }

    // Apply the saved language on app startup
    fun applySavedLanguage() {
        val savedLanguage = getLanguage()
        changeLanguage(context, savedLanguage)
    }

    // Save login state
    fun saveLoginState(isLoggedIn: Boolean) {
        preferences.edit {
            putBoolean(IS_LOGGED_IN_KEY, isLoggedIn)
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(IS_LOGGED_IN_KEY, false)
    }

    // Clear all session data (logout)
    fun clearSession() {
        preferences.edit {
            clear()
        }
    }

    fun setAuthToken(token:String){
        preferences.edit {
            putString(AppConstant.AuthToken, token)
        }
    }

    fun setUserId(userId:Int){
        preferences.edit {
            putInt(AppConstant.UserId, userId)
        }
    }

    fun setUserName(name :String){
        preferences.edit {
            putString(AppConstant.NAME, name)
        }
    }

    fun getUserName():String?{
        return preferences.getString(AppConstant.AuthToken,"")
    }

    fun getUserId() :Int{
        return preferences.getInt(AppConstant.UserId,-1)
    }
    fun getAuthToken():String?{
        return preferences.getString(AppConstant.AuthToken,"")
    }
}
