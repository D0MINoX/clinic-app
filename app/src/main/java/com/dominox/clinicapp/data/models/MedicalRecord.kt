package com.dominox.clinicapp.data.models

data class MedicalRecord(
    val id: Int,
    val patientId: Int,
    val doctorId: Int,
    val visitDate: String,
    val diagnosis: String? = null,
    val prescriptions: String? = null,
    val notes: String? = null
)