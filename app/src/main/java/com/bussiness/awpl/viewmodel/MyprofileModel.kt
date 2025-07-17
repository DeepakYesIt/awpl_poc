package com.bussiness.awpl.viewmodel

data class MyprofileModel(
    val age: Int,
    val basic_info: Int,
    val contact_no: String?,
    val created_at: String?,
    val ds_code: String,
    val email: String?,
    val fcm_token: String?,
    val gender: String?,
    val height: String?,
    val id: Int,
    val is_suspended: Int,
    val name: String?,
    val profile_path: String?,
    val referred_by: String?,
    val suspended_at: String?,
    val suspension_reason: String?,
    val updated_at: String?,
    val weight: String?,
    var state :String?
)