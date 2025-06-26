package com.bussiness.awpl.utils

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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

    fun to24Hour(time12h: String): String {
        // Regex: 1–2 digits hour, colon, 1–2 digits minute, optional spaces, AM/PM (any case)
        val regex = Regex("""^\s*(\d{1,2}):(\d{1,2})\s*([AaPp][Mm])\s*$""")
        val match = regex.matchEntire(time12h)
            ?: throw IllegalArgumentException("Invalid time format: \"$time12h\"")

        val (hStr, mStr, ampm) = match.destructured
        val hour12 = hStr.toInt().also {
            require(it in 1..12) { "Hour must be 1–12: found $it" }
        }
        val minute = mStr.toInt().also {
            require(it in 0..59) { "Minute must be 0–59: found $it" }
        }

        // Convert to 24h
        val hour24 = when {
            ampm.equals("AM", true) && hour12 == 12 -> 0      // 12 AM → 00
            ampm.equals("AM", true)                            -> hour12 // 1 AM–11 AM unchanged
            ampm.equals("PM", true) && hour12 < 12             -> hour12 + 12 // 1 PM–11 PM → 13–23
            else                                               -> 12    // 12 PM → 12
        }

        return String.format("%02d:%02d", hour24, minute)
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
            apply() // this is optional; the extension applies automatically
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

    fun setUserEmail(email :String?){
        preferences.edit{
            putString(AppConstant.EMAIL,email)
        }
    }

    fun getUserEmail():String{
        return preferences.getString(AppConstant.EMAIL,"")?:""
    }

    fun setUserName(name :String){
        preferences.edit {
            putString(AppConstant.NAME, name)
        }
    }
    fun setUserImage(url :String){
        preferences.edit {
            putString(AppConstant.IMAGE, url)
        }
    }

    fun getUserImage():String{
        return preferences.getString(AppConstant.IMAGE,"")?:""
    }

    fun getUserName():String?{
        return preferences.getString(AppConstant.NAME,"")
    }

    fun getUserId() :Int{
        return preferences.getInt(AppConstant.UserId,-1)
    }
    fun getAuthToken():String?{
        return preferences.getString(AppConstant.AuthToken,"")
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // On Android 13+ → check POST_NOTIFICATIONS permission
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // On Android 12 and below → check if user manually disabled notifications
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}
