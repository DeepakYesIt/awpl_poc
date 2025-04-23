package com.bussiness.awpl.model

data class Prescription(
                        val referred: String? = null,
                        val diagnosis: String = "",
                        val date: String = "",
                        val time: String = "")
