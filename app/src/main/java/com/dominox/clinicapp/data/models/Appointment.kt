package com.dominox.clinicapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val doctorName: String,
    val date: String,
    val time: String,
    val description: String,
    val status: String
)
