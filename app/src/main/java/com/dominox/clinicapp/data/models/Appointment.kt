package com.dominox.clinicapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
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
    val status: String,
    val recommendations: String? = null
)
@Serializable
data class AppointmentRequest(
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String,

)

@Serializable
@Parcelize
data class AppointmentResponse(
    val id: Int,
    val patientId: Int,
    val patientName: String?,
    val doctorId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String,
    val status: String,
    val recommendations: String? = null
): Parcelable {

}
