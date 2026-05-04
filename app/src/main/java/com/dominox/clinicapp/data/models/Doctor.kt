package com.dominox.clinicapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Doctor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val specialization: String? = null,
    val isActive: Boolean? = true
)

