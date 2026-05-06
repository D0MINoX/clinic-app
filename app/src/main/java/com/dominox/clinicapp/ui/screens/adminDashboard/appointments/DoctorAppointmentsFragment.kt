package com.dominox.clinicapp.ui.screens.adminDashboard.appointments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.api.TokenManager
import com.dominox.clinicapp.data.models.Appointment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DoctorAppointmentsFragment : Fragment(R.layout.fragment_doctor_appointments) {

    // klasa przechowująca dane dla grupy dnia
    data class DayGroup(val date: String, val appointments: List<Appointment>, var isExpanded: Boolean = false)

    @Inject lateinit var appointmentService: AppointmentService
    @Inject lateinit var tokenManager: TokenManager

    private lateinit var mainRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainRecyclerView = view.findViewById(R.id.doctorAppointmentsRecyclerView)
        mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val doctorId = tokenManager.getUserIdFromToken()
        if(doctorId != null){
            loadAppointments(doctorId)
        }

        view.findViewById<MaterialToolbar>(R.id.doctorAppointmentsToolbar)
            .setNavigationOnClickListener { findNavController().navigateUp() }

    }

    private fun loadAppointments(id: Int){
        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.getDoctorAppointments(id)
            result.onSuccess { list ->
                val grouped = list.groupBy { it.appointmentDate }
                    .map { DayGroup(it.key, it.value.sortedBy { a -> a.appointmentTime }) }
                    .sortedBy { it.date }

                mainRecyclerView.adapter = DoctorDayGroupAdapter(grouped)

            }.onFailure {
                Snackbar.make(requireView(), "Błąd pobierania wizyt", Snackbar.LENGTH_SHORT)
            }
        }
    }
}