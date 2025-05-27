package com.bussiness.awpl

import com.bussiness.awpl.viewmodel.DiseaseModel

object HealthDataStore {

    var healthNeeds1: List<DiseaseModel> = emptyList()

    fun saveHealthNeeds(list: List<DiseaseModel>) {
        healthNeeds1 = list
    }

    fun getHealthNeeds(): List<DiseaseModel> {
        return healthNeeds1
    }
}