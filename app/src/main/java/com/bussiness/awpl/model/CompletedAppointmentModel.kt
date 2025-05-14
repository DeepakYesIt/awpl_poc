package com.bussiness.awpl.model

data class CompletedAppointmentModel(
    val date: String,
    val doctorImage: String,
    val doctorName: String,
    val id: Int,
    val patient_id: Int,
    val time: String,
    val referred_name :String?,
    val chat_id : String?
)