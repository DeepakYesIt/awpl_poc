package com.bussiness.awpl.model

data class AppointmentModel(  val doctorName: String,
                              val appointmentDate: String,
                              val appointmentTime: String,
                              val consultationType: String,
                              val uploadDate: String,
                              val doctorImage: Int,
                              val isCancelled: Boolean = false
)
