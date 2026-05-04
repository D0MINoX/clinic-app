package com.dominox.clinicapp.api

import com.dominox.clinicapp.data.models.Appointment
import com.dominox.clinicapp.data.models.AppointmentRequest
import com.dominox.clinicapp.data.models.AppointmentResponse
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
    suspend fun getOccupiedSlots(doctorId: Int, date: String): Result<List<String>> {
        return try {
            val response = NetworkClient.httpClient.get("$BASE_URL/getOccupiedSlots") {
                parameter("doctorId", doctorId)
                parameter("appointmentDate", date)
                contentType(ContentType.Application.Json)
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Błąd: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun bookAppointment(request: AppointmentRequest): Result<AppointmentResponse> {
        return try {
            val response = NetworkClient.httpClient.post("https://api-kotlin.rosaryapi.pl/api/appointments/book") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Błąd: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}