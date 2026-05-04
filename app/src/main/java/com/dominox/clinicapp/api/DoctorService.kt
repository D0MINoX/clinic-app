package com.dominox.clinicapp.api

import com.dominox.clinicapp.data.models.Doctor
import com.dominox.clinicapp.network.NetworkClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject

class DoctorService @Inject constructor() {
    private  val  BASE_URL = "https://api-kotlin.rosaryapi.pl/api"

     suspend fun getDoctors(): Result<List<Doctor>> {
         return try {
             val response = NetworkClient.httpClient.get("$BASE_URL/getDoctors") {
                 contentType(ContentType.Application.Json)
             }

             if (response.status == HttpStatusCode.OK) {
                 val doctors = response.body<List<Doctor>>()
                 Result.success(doctors)
             } else {
                 Result.failure(Exception("Błąd serwera: ${response.status}"))
             }
         } catch (e: Exception) {
             Result.failure(e)
         }
    }
}