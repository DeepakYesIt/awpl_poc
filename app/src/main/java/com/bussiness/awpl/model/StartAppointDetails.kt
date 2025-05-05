package com.bussiness.awpl.model

data class StartAppointDetails(
    val date: String,
    val doctorImage: String,
    val doctorName: String,
    val id: Int,
    val patient_id: Int,
    val time: String
)