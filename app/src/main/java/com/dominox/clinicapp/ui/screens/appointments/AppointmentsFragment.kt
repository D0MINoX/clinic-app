package com.dominox.clinicapp.ui.screens.appointments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.api.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class AppointmentsFragment : Fragment(R.layout.fragment_appointments) {

    @Inject
    lateinit var appointmentService: AppointmentService
    @Inject lateinit var tokenManager: TokenManager
    private val appointmentsAdapter = AppointmentsAdapter(emptyList())
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.appointmentsRecyclerView)
        recyclerView.adapter = appointmentsAdapter

        view.findViewById<MaterialToolbar>(R.id.appointmentsToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val patientId = tokenManager.getUserIdFromToken()

        if (patientId != null) {
            loadAppointments(patientId)
        } else {
            // Jeśli nie ma ID w tokenie, coś jest nie tak (np. token wygasł)
            Snackbar.make(view, "Błąd autoryzacji. Zaloguj się ponownie.", Snackbar.LENGTH_LONG).show()
        }
        view.findViewById<Button>(R.id.bookAppointmentButton).setOnClickListener {
            //Snackbar.make(view, R.string.appointments_book_action_placeholder, Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.bookAppointment)
        }
    }
    private fun loadAppointments(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.getPatientAppointments(id)

            result.onSuccess { list ->
                appointmentsAdapter.updateData(list)

                if (list.isEmpty()) {
                    Snackbar.make(requireView(), "Brak zaplanowanych wizyt", Snackbar.LENGTH_SHORT).show()
                }
            }.onFailure {
                Snackbar.make(requireView(), "Nie udało się pobrać wizyt", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
