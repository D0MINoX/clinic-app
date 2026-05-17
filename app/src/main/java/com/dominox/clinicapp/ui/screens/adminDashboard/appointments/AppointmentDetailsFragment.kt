package com.dominox.clinicapp.ui.screens.adminDashboard.appointments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.data.models.AppointmentResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppointmentDetailsFragment : Fragment(R.layout.fragment_appointment_details) {

    @Inject
    lateinit var appointmentService: AppointmentService

    private var appointment: AppointmentResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Odbieramy przesłany obiekt
        appointment = arguments?.getParcelable("appointment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.detailsToolbar)
        val patientName = view.findViewById<TextView>(R.id.detailPatientName)
        val dateTime = view.findViewById<TextView>(R.id.detailDateTime)
        val prescriptionsInput = view.findViewById<TextInputEditText>(R.id.prescriptionsEditText)
        val saveButton = view.findViewById<MaterialButton>(R.id.saveAppointmentButton)

        // Obsługa powrotu
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Sprawdzamy czy dane dotarły
        val app = appointment
        if (app == null) {
            Snackbar.make(view, "Błąd: Nie znaleziono danych wizyty", Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
            return
        }

        // Wypełnianie widoków danymi
        patientName.text = app.patientName ?: "Nieznany pacjent"
        dateTime.text = "Data: ${app.appointmentDate}, Godzina: ${app.appointmentTime}"
        prescriptionsInput.setText(app.recommendations)

        // JEDEN wspólny Listener dla przycisku zapisu
        saveButton.setOnClickListener {
            val text = prescriptionsInput.text.toString()

            if (text.isBlank()) {
                Snackbar.make(view, "Wpisz zalecenia przed zakończeniem wizyty", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Wywołujemy zapis na serwerze
            completeAppointment(app.id, text)
        }
    }

    private fun completeAppointment(appointmentId: Int, text: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.completeAppointment(appointmentId, text)

            result.onSuccess {
                Snackbar.make(requireView(), "Wizyta została zakończona i zapisana", Snackbar.LENGTH_SHORT).show()

                // Opóźnienie przed powrotem
                delay(1000)
                findNavController().navigateUp()

            }.onFailure { error ->
                Snackbar.make(requireView(), "Błąd: ${error.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}