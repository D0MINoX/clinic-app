package com.dominox.clinicapp.api

import com.dominox.clinicapp.network.NetworkClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class LiveUpdatesService @Inject constructor() {
    private val WS_URL = "wss://api-kotlin.rosaryapi.pl/ws/appointments"

    fun observeSlotTakenEvents(): Flow<Pair<String, String>> = flow {
        NetworkClient.httpClient.webSocket(WS_URL) {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    try {
                        val json = JSONObject(text)
                        if (json.optString("event") == "SLOT_TAKEN") {
                            val doctorId = json.getInt("doctorId")
                            val date = json.getString("date")
                            val time = json.getString("time")

                            val key = "${doctorId}_${date}"
                            emit(key to time)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }.catch { e ->
        e.printStackTrace()
    }
}