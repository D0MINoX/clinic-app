package com.dominox.clinicapp.data.models
import kotlinx.serialization.Serializable
@Serializable
data class LoginRequest(
    val email: String,
    val passwordHash: String
)
