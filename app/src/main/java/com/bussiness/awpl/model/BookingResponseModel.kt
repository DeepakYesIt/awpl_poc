package com.bussiness.awpl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingResponseModel(
    val appointment_id: Int,
    val date: String,
    val doctor_list: List<Doctor>,
    val dummy_amount: String,
    val is_first_consultation: Boolean,
    val payment_amount: String,
    val time: String
): Parcelable