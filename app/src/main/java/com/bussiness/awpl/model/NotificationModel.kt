package com.bussiness.awpl.model

data class NotificationModel(
    val title: String,
    val description: String,
    val time: String,
    val icon: Int,
    val date: String,
    var isRead: Boolean = false
)
