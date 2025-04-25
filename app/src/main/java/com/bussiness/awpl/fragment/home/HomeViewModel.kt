package com.bussiness.awpl.fragment.home

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.R
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.model.OrganDeptModel

class HomeViewModel : ViewModel() {

    val organList = listOf(
        OrganDeptModel(R.drawable.cadiologist, "Heart"),
        OrganDeptModel(R.drawable.dental, "Dental"),
        OrganDeptModel(R.drawable.kidney, "Kidney"),
        OrganDeptModel(R.drawable.gastroenterologists, "Stomach"),
        OrganDeptModel(R.drawable.dental, "Dental") ,
         OrganDeptModel(R.drawable.ic_tb, "Tuberclosis(TB)"),
         OrganDeptModel(R.drawable.ic_allergy, "Allergy"),
        OrganDeptModel(R.drawable.ic_blood_pressure, "Blood\nPressure"),
        OrganDeptModel(R.drawable.ic_typhoid, "Typhoid"),
        OrganDeptModel(R.drawable.ic_liver, "Liver"),
        OrganDeptModel(R.drawable.ic_thyroid, "Thyroid"),
        OrganDeptModel(R.drawable.ic_gyno, "Gynic\nDisease"),
    )

    val healthJourneyList = listOf(
        HealthJourneyItem("Begin Your Health\n Journey with a \nFree Consultation!", R.drawable.women_doctor),
        HealthJourneyItem("Bringing Doctors\n to Your Door – \nVirtually.", R.drawable.women_doctor),
        HealthJourneyItem("Upload Symptoms \nfor Minor Issues \nand Major Concerns", R.drawable.women_doctor)
    )

    val browseVideoList = listOf(
        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail),
        HealthJourneyItem("Lorem ipsum dolor sit \n amet, consectetur \n adipiscing elit, ", R.drawable.video_thumbnail)
    )
}
