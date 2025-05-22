package com.bussiness.awpl.model

data class PatinetNotification(
    var date: String,
    val description: String,
    val id: String,
    val readStatus: String,
    val time: String,
    val title: String,
    val type: String
)