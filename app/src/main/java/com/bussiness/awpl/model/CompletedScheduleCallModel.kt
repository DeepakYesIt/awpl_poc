package com.bussiness.awpl.model

data class CompletedScheduleCallModel(
    val chat_id: String?,
    val date: String,
    val doctorImage: String?,
    val doctorName: String?,
    val id: Int,
    val patient_id: Int,
    val referred_name: String?,
    val time: String?
)