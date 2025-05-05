package com.bussiness.awpl.viewmodel

import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseModel(
    val category: String,
    val icon_path: String,
    val id: Int,
    val name: String
) :  Parcelable()