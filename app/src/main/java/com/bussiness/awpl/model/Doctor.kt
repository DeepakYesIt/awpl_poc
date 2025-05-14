package com.bussiness.awpl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    val experience: Int,
    val experience_yrs: String,
    val name: String,
    val profile_path: String
) : Parcelable