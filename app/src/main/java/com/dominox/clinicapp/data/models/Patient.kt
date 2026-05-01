package com.dominox.clinicapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "patients")
@Serializable
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val phoneNumber: String, // Zmienione z phone na phoneNumber
    val dateOfBirth: String,
    val address: String,
    val passwordHash: String // Zmienione z password na passwordHash
)