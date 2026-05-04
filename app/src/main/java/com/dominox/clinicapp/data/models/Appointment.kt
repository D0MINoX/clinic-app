package com.dominox.clinicapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.ktor.websocket.CloseReason
import kotlinx.serialization.Serializable

@Entity(tableName = "appointments")
@Serializable
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String,
    val status: String
)
@Serializable
data class AppointmentRequest(
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String
)

@Serializable
data class AppointmentResponse(
    val id: Int,
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String,
    val status: String
)
