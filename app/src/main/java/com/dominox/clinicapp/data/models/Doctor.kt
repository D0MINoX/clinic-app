package com.dominox.clinicapp.data.models

data class Doctor(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val specialization: String,
    val isActive: Boolean
)
