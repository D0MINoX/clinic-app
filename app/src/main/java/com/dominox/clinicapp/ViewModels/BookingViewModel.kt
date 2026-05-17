package com.dominox.clinicapp.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.api.LiveUpdatesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val appointmentService: AppointmentService,
    private val liveUpdatesService: LiveUpdatesService
) : ViewModel() {

    private val _occupiedSlots = MutableStateFlow<List<String>>(emptyList())
    val occupiedSlots: StateFlow<List<String>> = _occupiedSlots.asStateFlow()

    private var currentDoctorId: Int = -1
    private var currentDate: String = ""

    init {
        viewModelScope.launch {
            liveUpdatesService.observeSlotTakenEvents().collect { (key, newTime) ->
                // Bardzo ważne: sprawdzamy czy to event dla aktualnie wyświetlanego lekarza i daty
                val expectedKey = "${currentDoctorId}_${currentDate}"
                if (key == expectedKey) {
                    val dateFromKey = key.substringAfter("_")
                    val formattedSlot = "${dateFromKey}T${newTime}"

                    // Używamy zestawu (Set), aby uniknąć duplikatów
                    _occupiedSlots.update { current ->
                        (current + formattedSlot).distinct()
                    }
                }
            }
        }
    }

    fun loadSlots(doctorId: Int, date: String) {

        _occupiedSlots.value = emptyList()

        currentDoctorId = doctorId
        currentDate = date

        viewModelScope.launch {
            Log.d("DEBUG_SLOTS", "Rozpoczynam pobieranie dla lekarza $doctorId na dzień $date")
            val result = appointmentService.getOccupiedSlots(doctorId, date)

            result.onSuccess { slots ->
                Log.d("DEBUG_SLOTS", "Pobrano z API: ${slots.size} zajętych slotów")
                if (currentDate == date && currentDoctorId == doctorId) {
                    _occupiedSlots.value = slots
                }
            }.onFailure { error ->
                Log.e("DEBUG_SLOTS", "Błąd API: ${error.message}")
                // W razie błędu wysyłamy pustą listę, żeby Fragment przestał wyświetlać "Ładowanie..."
                _occupiedSlots.value = emptyList()
            }
        }
    }
}