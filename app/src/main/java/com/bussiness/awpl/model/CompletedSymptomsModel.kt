package com.bussiness.awpl.model

data class CompletedSymptomsModel(
    val file_path: String?,
    val prescription_id: Int,
    val symptom_id: Int,
    val upload_date: String?
)