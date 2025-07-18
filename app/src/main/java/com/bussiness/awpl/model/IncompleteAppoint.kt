package com.bussiness.awpl.model

data class IncompleteAppoint(
    val date: String,
    val doctorImage: String,
    val doctorName: String,
    val ds_code: String,
    val id: Int,
    val patient_id: Int,
    val time: String,
    val can_reschedule :Boolean = true,
    val is_free_call : Boolean = false,
)