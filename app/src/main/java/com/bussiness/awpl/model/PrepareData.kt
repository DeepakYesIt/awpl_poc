package com.bussiness.awpl.model

import com.bussiness.awpl.R

object  PrepareData {


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
        fun getDummyPrescriptions(): MutableList<Prescription> {
            return mutableListOf(
                Prescription(
                    referred = "Referred Name: Johnson",
                    diagnosis = " Seasonal allergies",
                    date = "Thu May 14",
                    time = "10:00 - 10:15 AM"
                ),
                Prescription(
                    referred = null,
                    diagnosis = " Migraine",
                    date = "Fri May 15",
                    time = "11:30 - 11:45 AM"
                ),
                Prescription(
                    referred = "Referred Name: Dr. Mehta",
                    diagnosis = " Back pain",
                    date = "Sat May 16",
                    time = "01:00 - 01:15 PM"
                ),
                Prescription(
                    referred = "Referred Name: Clinic XYZ",
                    diagnosis = " Stomach infection",
                    date = "Sun May 17",
                    time = "02:15 - 02:30 PM"
                ),
                Prescription(
                    referred = "Referred Name: Dr. Kim",
                    diagnosis = " Mild asthma",
                    date = "Mon May 18",
                    time = "09:00 - 09:15 AM"
                )
            )
        }

    fun filterPrescriptionsByReferred(prescriptions: List<Prescription>): MutableList<Prescription> {
        return prescriptions.filter { !it.referred.isNullOrBlank() }.toMutableList()
    }

    fun getSelfReferredPrescriptions(prescriptions: List<Prescription>): MutableList<Prescription> {
        return prescriptions.filter { it.referred == null }.toMutableList()
    }

}