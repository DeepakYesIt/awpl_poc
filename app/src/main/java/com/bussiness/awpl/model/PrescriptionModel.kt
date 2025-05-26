package com.bussiness.awpl.model

data class PrescriptionModel(
    val chat_id: Any,
    val date: String?,
    val diagnosis: String?,
    val file_path: String?,
    val prescription_id: Int,
    val time: String?,
    val referred_name :String?
)