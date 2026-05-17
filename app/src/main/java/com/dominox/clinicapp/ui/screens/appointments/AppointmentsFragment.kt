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
import com.dominox.clinicapp.api.DoctorService
import com.dominox.clinicapp.api.TokenManager
import com.dominox.clinicapp.data.models.Appointment
import com.dominox.clinicapp.data.models.Patient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.http.ContentDisposition.Companion.File
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
@AndroidEntryPoint
class AppointmentsFragment : Fragment(R.layout.fragment_appointments) {

    @Inject lateinit var appointmentService: AppointmentService
    @Inject lateinit var doctorService : DoctorService
    @Inject lateinit var tokenManager: TokenManager
    private val appointmentsAdapter = AppointmentsAdapter(emptyList(), emptyMap())
    private val completedAppointmentsAdapter = AppointmentsAdapter(emptyList(), emptyMap())
    private var currentAppointments : List<Appointment> = emptyList()
    private var doctorsMap: Map<Int, String> = emptyMap()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.appointmentsRecyclerView)
        recyclerView.adapter = appointmentsAdapter
        val completedRecyclerView = view.findViewById<RecyclerView>(R.id.completedAppointmentsRecyclerView)
        completedRecyclerView.adapter = completedAppointmentsAdapter

        //rozwijanie i zwijanie listy wizyt
        val upcomingButton = view.findViewById<MaterialButton>(R.id.upcomingAppointmentsButton)
        upcomingButton.setOnClickListener {
            val isVisible = recyclerView.visibility == View.VISIBLE
            recyclerView.visibility = if (isVisible) View.GONE else View.VISIBLE
            upcomingButton.setIconResource(if (isVisible) R.drawable.ic_add_24 else R.drawable.ic_remove_24)
        }

        val completedButton = view.findViewById<MaterialButton>(R.id.completedAppointmentsButton)
        completedButton.setOnClickListener {
            val isVisible = completedRecyclerView.visibility == View.VISIBLE
            completedRecyclerView.visibility = if (isVisible) View.GONE else View.VISIBLE
            completedButton.setIconResource(if (isVisible) R.drawable.ic_add_24 else R.drawable.ic_remove_24)
        }


        view.findViewById<MaterialToolbar>(R.id.appointmentsToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val patientId = tokenManager.getUserIdFromToken()

        if (patientId != null) {
            loadDoctorsAndAppointments(patientId)
        } else {
            // Jeśli nie ma ID w tokenie, coś jest nie tak (np. token wygasł)
            Snackbar.make(view, "Błąd autoryzacji. Zaloguj się ponownie.", Snackbar.LENGTH_LONG).show()
        }
        view.findViewById<Button>(R.id.bookAppointmentButton).setOnClickListener {
            findNavController().navigate(R.id.doctorsListFragment)
        }

        // pobieranie raportu
        view.findViewById<Button>(R.id.downloadReportButton)?.setOnClickListener {
            if (currentAppointments.isNotEmpty()){
                generateReport(currentAppointments)
            }else{
                Snackbar.make(requireView(), "Brak wizyt do pobrania", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadAppointments(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.getPatientAppointments(id)

            result.onSuccess { list ->
                currentAppointments = list

                val (upcomingAppointments, completedAppointments) = splitAppointmentsByDate(list)

                appointmentsAdapter.updateData(upcomingAppointments, doctorsMap)
                completedAppointmentsAdapter.updateData(completedAppointments, doctorsMap)

                if (list.isEmpty()) {
                    Snackbar.make(requireView(), "Brak zaplanowanych wizyt", Snackbar.LENGTH_SHORT).show()
                }
            }.onFailure {
                Snackbar.make(requireView(), "Nie udało się pobrać wizyt", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDoctorsAndAppointments(patientId: Int){
        viewLifecycleOwner.lifecycleScope.launch {
            val result = doctorService.getDoctors()
            result.onSuccess { list ->
                doctorsMap = list.associate { it.id to "${it.firstName} ${it.lastName}" }
                loadAppointments(patientId)
            }
        }
    }

    private fun generateReport(appointments: List<Appointment>) {
        val sb = StringBuilder()
        sb.append("\tRAPORT WIZYT PACJENTA:\n")
        sb.append("\t\tWygenerowano: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\n")
        sb.append("------------------------------------------------------------------------\n\n")

        appointments.forEach { appointment ->
            val doctorName = doctorsMap[appointment.doctorId] ?: "Nieznany"
            sb.append("Data: ${appointment.appointmentDate}\n")
            sb.append("Godzina: ${appointment.appointmentTime}\n")
            sb.append("Lekarz: ${doctorName}\n")
            sb.append("Powod: ${appointment.reason}\n")
            sb.append("Status: ${appointment.status}\n\n")
            sb.append("------------------------------------------------------------------------\n\n")
        }

        try{
            val fileName = "raport_wzyt_${System.currentTimeMillis()}.txt"
            val storage = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)

            val appFolder = File(storage, "KlinikaZdrowia")
            if(!appFolder.exists()) appFolder.mkdirs()

            val file = File(appFolder, fileName)
            file.writeText(sb.toString())

            Snackbar.make(requireView(), "Pobrano raport: $fileName", Snackbar.LENGTH_LONG)
                .setAction("OK") {}
                .show()
        }catch (e: Exception){
            Snackbar.make(requireView(), "Wystąpił błąd podczas generowania raportu", Snackbar.LENGTH_LONG)
        }


    }

    private fun splitAppointmentsByDate(appointments: List<Appointment>): Pair<List<Appointment>, List<Appointment>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val now = LocalDateTime.now()

        val (upcoming, completed) = appointments.partition { appointment ->
            val appointmentDateTime = LocalDateTime.parse(
                "${appointment.appointmentDate} ${appointment.appointmentTime}",
                formatter
            )
            !appointmentDateTime.isBefore(now)
        }

        return Pair(
            upcoming.sortedBy { appointment ->
                LocalDateTime.parse(
                    "${appointment.appointmentDate} ${appointment.appointmentTime}",
                    formatter
                )
            },
            completed.sortedByDescending { appointment ->
                LocalDateTime.parse(
                    "${appointment.appointmentDate} ${appointment.appointmentTime}",
                    formatter
                )
            }
        )
    }
}
