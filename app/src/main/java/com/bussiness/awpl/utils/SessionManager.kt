package com.bussiness.awpl.utils

import android.Manifest
import com.google.gson.reflect.TypeToken
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Locale
import androidx.core.content.edit
import com.bussiness.awpl.model.HolidayModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun shouldShow(dateStr: String, timeRange: String): Boolean {
        // Format input date
        return try {
            val locale = Locale.ENGLISH
            val dateOnlyFormat = SimpleDateFormat("dd MMM yy", locale)
            val dateTimeFormat = SimpleDateFormat("dd MMM yy hh:mm a", locale)

            val currentDateTime = Date()
            Log.d("TimeCheck", "Current datetime: $currentDateTime")

            val inputDate = dateOnlyFormat.parse(dateStr)
            Log.d("TimeCheck", "Parsed input date: $inputDate")

            if (inputDate == null) {
                Log.d("TimeCheck", "Input date parsing failed.")
                return false
            }

            // If current date is before input date
            if (currentDateTime.before(inputDate)) {
                Log.d("TimeCheck", "Current date is before input date. Returning false.")
                return false
            }

            // Prepare full datetime string
            val startTimeRaw = timeRange.split("-").first().trim()          // "01:00"
            val amPm = timeRange.takeLast(2)                                // "PM"
            val fullDateTimeStr = "$dateStr $startTimeRaw $amPm"           // "14 Jul 25 01:00 PM"

            Log.d("TimeCheck", "Full datetime string to parse: $fullDateTimeStr")

            val startDateTime = dateTimeFormat.parse(fullDateTimeStr)
            Log.d("TimeCheck", "Parsed start datetime: $startDateTime")

            if (startDateTime == null) {
                Log.d("TimeCheck", "Start time parsing failed.")
                return false
            }

            val result = currentDateTime >= startDateTime
            Log.d("TimeCheck", "Comparison result: $result")
            return result

        } catch (e: Exception) {
            Log.e("TimeCheck", "Exception occurred: ${e.message}", e)
            return false
        }
    }


    fun isMoreThanFiveMinutesPastStartTime(dateStr: String, timeRange: String): Boolean {
        return try {
            val locale = Locale.ENGLISH
            val dateTimeFormat = SimpleDateFormat("dd MMM yy hh:mm a", locale)
            val dateOnlyFormat = SimpleDateFormat("dd MMM yy", locale)

            val currentDateTime = Date()
            Log.d("TimeCheck", "Current datetime: $currentDateTime")

            val inputDate = dateOnlyFormat.parse(dateStr)
            Log.d("TimeCheck", "Parsed input date: $inputDate")

            if (inputDate == null || currentDateTime.before(inputDate)) {
                Log.d("TimeCheck", "Current date is before input date. Returning false.")
                return false
            }

            val startTimeRaw = timeRange.split("-").getOrNull(0)?.trim()
            val amPm = timeRange.takeLast(2) // "AM" or "PM"
            val fullStartDateTimeStr = "$dateStr $startTimeRaw $amPm"

            Log.d("TimeCheck", "Full start datetime string: $fullStartDateTimeStr")

            val startDateTime = dateTimeFormat.parse(fullStartDateTimeStr)
            Log.d("TimeCheck", "Parsed start datetime: $startDateTime")

            if (startDateTime == null) {
                Log.d("TimeCheck", "Failed to parse start datetime.")
                return false
            }

            val diffInMillis = currentDateTime.time - startDateTime.time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            Log.d("TimeCheck", "Difference in minutes: $diffInMinutes")

            val result = diffInMinutes >= 15
            Log.d("TimeCheck", "Is more than 15 minutes past start time: $result")
            result
        } catch (e: Exception) {
            Log.e("TimeCheck", "Exception: ${e.message}", e)
            false
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

    fun setTime(time :String){
        preferences.edit {
            putString(AppConstant.TIME, time)
        }
    }
    fun getTime() :  String{
        return preferences.getString(AppConstant.TIME,"")?:""

    }

    fun setDate(date :String){
        preferences.edit {
            putString(AppConstant.DATE, date)
        }
    }

    fun getDate() : String{
        return preferences.getString(AppConstant.DATE,"")?:""
    }

    fun setAuthToken(token:String){
        preferences.edit {
            putString(AppConstant.AuthToken, token)
        }
    }
    fun setAppointmentId(id:Int){
        preferences.edit {
            putInt(AppConstant.AppoitmentId,id)
        }
    }

    fun getAppointment():Int{
        return preferences.getInt(AppConstant.AppoitmentId,-1)
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
    fun setUserState(state:String){
        preferences.edit{
            putString(AppConstant.STATE,state)
        }
    }

    fun getUserState():String{
        return preferences.getString(AppConstant.STATE,"")?:""
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


    fun saveHolidayList(holidays: List<HolidayModel>) {
         val gson = Gson()
        val json = gson.toJson(holidays)
        preferences.edit {
            putString("HOLIDAY_LIST", json)
        }


    }

    fun getHolidayList(): List<HolidayModel> {
        val json = preferences.getString("HOLIDAY_LIST", null)
        val gson = Gson()
        return if (json != null) {
            val type = object : TypeToken<List<HolidayModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveStringList(key: String, list: List<String>) {
        var gson = Gson()
        val json = gson.toJson(list)
        preferences.edit().putString(key, json).apply()
    }

    // ✅ Get the string list
    fun getStringList(key: String): MutableList<String> {
        var gson = Gson()
        val json = preferences.getString(key, null)
        if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(json, type)
        }
        return mutableListOf<String>()
    }

    // ✅ Check if list exists
    fun isListPresent(key: String): Boolean {
        val list = getStringList(AppConstant.FEEDBACK)
        return list.contains(key)

    }




}
