package com.dominox.clinicapp.ViewModels

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
                val expectedKey = "${currentDoctorId}_${currentDate}"

                if (key == expectedKey) {
                    val dateFromKey = key.substringAfter("_")
                    val formattedSlot = "${dateFromKey}T${newTime}"
                    _occupiedSlots.update { current -> current + formattedSlot }
                }
            }
        }
    }

    fun loadSlots(doctorId: Int, date: String) {
        currentDoctorId = doctorId
        currentDate = date

        viewModelScope.launch {
            val result = appointmentService.getOccupiedSlots(doctorId, date)
            result.onSuccess { slots ->
                _occupiedSlots.value = slots
            }
        }
    }
}