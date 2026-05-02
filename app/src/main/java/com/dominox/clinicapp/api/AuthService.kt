package com.dominox.clinicapp.api
import com.dominox.clinicapp.data.models.LoginRequest
import com.dominox.clinicapp.data.models.Patient
import com.dominox.clinicapp.network.NetworkClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.call.*

class AuthService {
    // 10.0.2.2 to specjalny adres IP emulatora wskazujący na localhost Twojego komputera
    private val BASE_URL = "https://api-kotlin.rosaryapi.pl/api/"

    suspend fun register(patient: Patient): Result<String> {
        return try {
            val response = NetworkClient.httpClient.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(patient)
            }

            if (response.status == HttpStatusCode.Created) {
                Result.success("Zarejestrowano pomyślnie")
            } else {
                Result.failure(Exception("Błąd serwera: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun login(loginRequest: LoginRequest): Result<String> {
        return try {
            val response = NetworkClient.httpClient.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                val body = response.bodyAsText()
                Result.success(body)
            } else {
                Result.failure(Exception("Błędny e-mail lub hasło"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}