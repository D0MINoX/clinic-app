package com.dominox.clinicapp.api

import com.dominox.clinicapp.data.models.Appointment
import com.dominox.clinicapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class AppointmentService @Inject constructor() {
    // Adres Twojego API
    private val BASE_URL = "https://api-kotlin.rosaryapi.pl/api/appointments"

    suspend fun getPatientAppointments(patientId: Int): Result<List<Appointment>> {
        return try {
            val response = NetworkClient.httpClient.get("$BASE_URL/patient/$patientId") {
                contentType(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                val appointments = response.body<List<Appointment>>()
                Result.success(appointments)
            } else {
                Result.failure(Exception("Błąd serwera: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}