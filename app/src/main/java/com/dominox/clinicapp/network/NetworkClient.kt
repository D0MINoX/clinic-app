package com.dominox.clinicapp.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import android.util.Log
import io.ktor.client.plugins.logging.*
object NetworkClient {
    val httpClient = HttpClient{
        install(ContentNegotiation){
            json(Json { ignoreUnknownKeys=true
            prettyPrint=true})

        }
        install(Logging) {
            // To sprawi, że logi trafią do Logcata
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP_CLIENT", message)
                }
            }
            level = LogLevel.BODY
        }
    }
}