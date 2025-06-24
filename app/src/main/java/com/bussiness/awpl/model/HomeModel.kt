package com.bussiness.awpl.model

import com.bussiness.awpl.viewmodel.DiseaseModel

data class HomeModel(
    val healthNeeds: List<DiseaseModel>,
    val startAppointDetails: StartAppointDetails?,
    val upcomingAppointDetails: UpcomingAppointDetails?,
    val videos: List<HealthJourneyItem>,
    var notification_present :Boolean = false
)