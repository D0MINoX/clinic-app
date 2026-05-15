package com.dominox.clinicapp.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.engine.okhttp.OkHttp
object NetworkClient {
    val httpClient = HttpClient(OkHttp){
        install(ContentNegotiation){
            json(Json { ignoreUnknownKeys=true
            prettyPrint=true})

        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP_CLIENT", message)
                }
            }
            level = LogLevel.BODY
        }
        install(WebSockets) {
            pingInterval = 15_000
        }
    }
}