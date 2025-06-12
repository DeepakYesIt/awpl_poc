package com.bussiness.awpl.model

data class PrescriptionModel(
    val chat_id: String?,
    val date: String?,
    val diagnosis: String?,
    val file_path: String?,
    val prescription_id: Int,
    val time: String?,
    val referred_name :String?,
    val appointment_id :Int
)