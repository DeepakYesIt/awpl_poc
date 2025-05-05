package com.bussiness.awpl.utils

import com.bussiness.awpl.viewmodel.DiseaseModel

object DiseaseStore {

    private val diseaseList = mutableListOf<DiseaseModel>()

    fun setDiseases(diseases: List<DiseaseModel>) {
        diseaseList.clear()
        diseaseList.addAll(diseases)
    }

    fun getDiseases(): List<DiseaseModel> {
        return diseaseList
    }

}