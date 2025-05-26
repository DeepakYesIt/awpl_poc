package com.bussiness.awpl.model

import java.io.Serializable

data class PayuPaymentModel(
    val amount: String?,
    val email: String?,
    val firstname: String?,
    val furl: String?,
    val hash: String?,
    val key: String?,
    val phone: String?,
    val productinfo: String?,
    val surl: String?,
    val txnid: String?
) : Serializable