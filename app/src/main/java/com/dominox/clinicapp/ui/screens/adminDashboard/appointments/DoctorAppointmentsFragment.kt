package com.dominox.clinicapp.ui.screens.adminDashboard.appointments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.api.TokenManager
import com.dominox.clinicapp.data.models.AppointmentResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class DoctorAppointmentsFragment : Fragment(R.layout.fragment_doctor_appointments) {

    // Klasa przechowująca dane dla grupy dnia
    data class DayGroup(
        val date: String,
        val appointments: List<AppointmentResponse>,
        var isExpanded: Boolean = false
    )

    @Inject lateinit var appointmentService: AppointmentService
    @Inject lateinit var tokenManager: TokenManager

    private lateinit var mainRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainRecyclerView = view.findViewById(R.id.doctorAppointmentsRecyclerView)
        mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val doctorId = tokenManager.getUserIdFromToken()
        if (doctorId != null) {
            loadAppointments(doctorId)
        }

        view.findViewById<MaterialToolbar>(R.id.doctorAppointmentsToolbar)
            .setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun loadAppointments(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.getDoctorAppointments(id)
            result.onSuccess { list ->
                // 1. POPRAWIONE FILTROWANIE (Widzisz wszystkie dzisiejsze wizyty + przyszłe)
                val today = LocalDate.now()

                val filteredList = list.filter { appointment ->
                    try {
                        val appointmentDate = LocalDate.parse(appointment.appointmentDate)
                        // Pokazuj jeśli data jest dzisiaj lub w przyszłości
                        !appointmentDate.isBefore(today)
                    } catch (e: Exception) {
                        true // Jeśli błąd parsowania, zostaw na liście dla bezpieczeństwa
                    }
                }

                // 2. GRUPOWANIE I SORTOWANIE (Wg daty i godziny)
                val grouped = filteredList.groupBy { it.appointmentDate }
                    .map { entry ->
                        DayGroup(
                            date = entry.key,
                            appointments = entry.value.sortedBy { it.appointmentTime }
                        )
                    }
                    .sortedBy { it.date }

                // 3. ADAPTER Z NAWIGACJĄ (Używamy Parcelable dla całego obiektu)
                mainRecyclerView.adapter = DoctorDayGroupAdapter(grouped) { clickedAppointment ->
                    val bundle = Bundle().apply {
                        // Przesyłamy cały obiekt (wymaga @Parcelize w AppointmentResponse)
                        putParcelable("appointment", clickedAppointment)
                    }

                    findNavController().navigate(
                        R.id.action_doctorAppointmentsFragment_to_appointmentDetailsFragment,
                        bundle
                    )
                }
            }.onFailure {
                Snackbar.make(requireView(), "Błąd pobierania wizyt", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}