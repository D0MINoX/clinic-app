package com.dominox.clinicapp.api
import com.dominox.clinicapp.data.models.LoginRequest
import com.dominox.clinicapp.data.models.Patient
import com.dominox.clinicapp.data.models.TokenResponse
import com.dominox.clinicapp.network.NetworkClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.call.*
import javax.inject.Inject

class AuthService @Inject constructor() {
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
            val response = NetworkClient.httpClient.post("${BASE_URL}login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                // Wyciągamy token z JSON-a (np. {"token": "eyJ..."})
                val tokenResponse = response.body<TokenResponse>()
                val token = tokenResponse.token

                // Zapisujemy token (tutaj potrzebujemy dostępu do SharedPreferences)
                Result.success(token)
            } else {
                val errorBody = response.bodyAsText()
                println("DEBUG_API: Status: ${response.status}, Body: $errorBody")
                Result.failure(Exception("Błąd: ${response.status}"))

            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}