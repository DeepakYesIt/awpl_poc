package com.bussiness.awpl.model

data class LoginModel(
    val basic_information: Int,
    val email: String?,
    val name: String?,
    val token: String?,
    val userId: Int,
    var profile_path:String?
)