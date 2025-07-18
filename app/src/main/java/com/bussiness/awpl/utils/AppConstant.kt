package com.bussiness.awpl.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object AppConstant {
    val LOCAL_NAME: String? ="LOCAL"
    val FEEDBACK :String ="FEEDBACK"
    val STATE: String? ="STATE"
    val BACK: String? ="BACK"
    val PDF: String?="PDF"
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
    val statesAndUTs = mutableListOf(
        "Andaman And Nicobar Islands",  // UT
        "Andhra Pradesh",               // State
        "Arunachal Pradesh",            // State
        "Assam",                        // State
        "Bihar",                        // State
        "Chandigarh",                   // UT
        "Chhattisgarh",                 // State
        "Dadra And Nagar Haveli And Daman And Diu",  // UT
        "Delhi",                        // UT / NCT
        "Goa",                          // State
        "Gujarat",                      // State
        "Haryana",                      // State
        "Himachal Pradesh",            // State
        "Jammu And Kashmir",           // UT
        "Jharkhand",                   // State
        "Karnataka",                   // State
        "Kerala",                      // State
        "Ladakh",                      // UT
        "Lakshadweep",                 // UT
        "Madhya Pradesh",              // State
        "Maharashtra",                 // State
        "Manipur",                     // State
        "Meghalaya",                   // State
        "Mizoram",                     // State
        "Nagaland",                    // State
        "Odisha",                      // State
        "Puducherry",                  // UT
        "Punjab",                      // State
        "Rajasthan",                   // State
        "Sikkim",                      // State
        "Tamil Nadu",                  // State
        "Telangana",                   // State
        "Tripura",                     // State
        "Uttar Pradesh",              // State
        "Uttarakhand",                // State
        "West Bengal"                 // State
    )


    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeMoreThanTwoHoursAhead(dateStr: String, timeRange: String): Boolean {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("dd MMM yy h:mm a", Locale.ENGLISH)

            val parts = timeRange.split("-")
            if (parts.size != 2) return false

            val startTimeRaw = parts[0].trim() // "08:00"
            val endTimeRaw = parts[1].trim()   // "08:15 PM"

            // Get AM/PM from end time
            val amPm = endTimeRaw.takeLast(2)  // "PM"

            // Append AM/PM to start time
            val normalizedStartTime = "$startTimeRaw $amPm" // "08:00 PM"

            // Combine date with time
            val fullDateTimeStr = "$dateStr $normalizedStartTime" // e.g., "15 Jul 25 08:00 PM"

            val inputDateTime = LocalDateTime.parse(fullDateTimeStr, inputFormatter)

            val now = LocalDateTime.now()

            return Duration.between(now, inputDateTime).toMinutes() > 120

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}