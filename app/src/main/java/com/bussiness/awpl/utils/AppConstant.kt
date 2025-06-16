package com.bussiness.awpl.utils

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object AppConstant {
    val Chat :String ="chat"
    val DOCTOR: String ="DOCTOR"
    val uid: String? ="uid"
    val CHANNEL_NAME: String?="channel_name"
    val AppoitmentId: String? ="appointment_id"
    val BOOK_MODEL: String? ="book_model"
    val ID: String? ="id"
    val FOR_ME: String? = "for_me"
    val OTHERS: String? ="others"
    val DISEASE_ID: String? ="disease_id"
    val DISEASE_LIST: String? ="disease_list"
    val Base_URL: String ="https://awplconnectadmin.tgastaging.com"
    val NAME: String? ="name"
    val AuthToken: String = "AUTH_TOKEN"
    val unKnownError = "There was an unknown error. Check your connection, and try again."
    val UserId ="user_id"
    var IMAGE ="IMAGE"
    var Age ="AGE"
    var Height ="height"
    var Weight ="weight"
    var FullName ="fullName"
    var Gender ="gender"
    var TYPE ="type"
    var DATE = "date"
    var TIME ="time"
    var APPID ="APPID"
    var EMAIL ="email"


    fun isTimeMoreThanTwoHoursAhead(dateStr: String, timeRange: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy h:mm a", Locale.ENGLISH)
            val todayYear = LocalDate.now().year

            // Combine start time with date
            val parts = timeRange.split("-")
            if (parts.size != 2) return false

            val startTime = parts[0].trim()       // e.g. 5:15
            val endTime = parts[1].trim()         // e.g. 5:30 PM

            // Append PM/AM from endTime to startTime if needed
            val amPm = endTime.takeLast(2)        // "PM"
            val normalizedStart = "$startTime $amPm"  // "5:15 PM"

            // Combine date and time
            val fullDateTimeStr = "$dateStr $todayYear $normalizedStart" // "Fri Jun 13 2025 5:15 PM"

            val inputDateTime = LocalDateTime.parse(fullDateTimeStr, formatter)
            val now = LocalDateTime.now()

            // Return true if it's more than 2 hours ahead
            Duration.between(now, inputDateTime).toMinutes() > 120

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}