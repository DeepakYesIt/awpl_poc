package com.bussiness.awpl.viewmodel


import java.io.Serializable


data class DiseaseModel(
    val category: String,
    val icon_path: String,
    val id: Int,
    val name: String
) : Serializable