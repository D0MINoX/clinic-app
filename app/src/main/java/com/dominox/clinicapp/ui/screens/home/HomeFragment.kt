package com.dominox.clinicapp.ui.screens.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.api.DoctorService
import com.dominox.clinicapp.api.TokenManager
import com.dominox.clinicapp.data.models.Appointment
import com.dominox.clinicapp.ui.screens.adminDashboard.appointments.DoctorAppointmentAdapter
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var doctorService : DoctorService
    @Inject lateinit var appointmentService: AppointmentService
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patientId = tokenManager.getUserIdFromToken()
        if (patientId != null) {
            loadNearestAppointment(patientId)
        }

        view.findViewById<TextView>(R.id.welcomeText).text = getString(R.string.home_welcome)
        //view.findViewById<TextView>(R.id.nextVisitText).text = getString(R.string.home_next_visit)

        val menuRecyclerView = view.findViewById<RecyclerView>(R.id.menuRecyclerView)
        val items = listOf(
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_my_calendar,
                title = getString(R.string.home_menu_book_appointment),
                description = getString(R.string.home_menu_book_appointment_desc),
                destinationId = R.id.doctorsListFragment
            ),
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_agenda,
                title = getString(R.string.home_menu_my_appointments),
                description = getString(R.string.home_menu_my_appointments_desc),
                destinationId = R.id.appointmentsFragment
            ),
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_info_details,
                title = getString(R.string.home_menu_history),
                description = getString(R.string.home_menu_history_desc),
                destinationId = R.id.medicalHistoryFragment
            ),
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_call,
                title = getString(R.string.home_menu_contact),
                description = getString(R.string.home_menu_contact_desc),
                isContactAction = true
            ),
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_preferences,
                title = getString(R.string.home_menu_settings),
                description = getString(R.string.home_menu_settings_desc),
                destinationId = R.id.settingsFragment
            )
        )

        menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        menuRecyclerView.adapter = HomeMenuAdapter(items) { item ->
            when {
                item.isContactAction -> openDialer()
                item.destinationId != null -> findNavController().navigate(item.destinationId)
            }
        }

        //wylogowanie
        view.findViewById<MaterialButton>(R.id.homeLogoutButton).setOnClickListener {
            logout()
        }
    }

    private fun openDialer() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.clinic_phone_uri)))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun loadNearestAppointment(patientId: Int){
        viewLifecycleOwner.lifecycleScope.launch {
            val doctorsResult = doctorService.getDoctors()
            val doctorsMap = doctorsResult.getOrNull()?.associate {
                it.id to "${it.firstName} ${it.lastName}"
            } ?: emptyMap()

            val result = appointmentService.getPatientAppointments(patientId)

            result.onSuccess { list ->
                val nearest = findNearest(list)
                updateUI(nearest, doctorsMap)
            }.onFailure {
                view?.findViewById<TextView>(R.id.nextVisitText)?.text = "Nie udało się pobrać wizyt"
            }
        }
    }

    private fun findNearest(appointments: List<Appointment>): Appointment? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val now = LocalDateTime.now()

        return appointments.filter {
            val dt = LocalDateTime.parse("${it.appointmentDate} ${it.appointmentTime}", formatter)
            dt.isAfter(now)
        }
        .minByOrNull {
            LocalDateTime.parse("${it.appointmentDate} ${it.appointmentTime}", formatter)
        }
    }

    private fun updateUI(nearest: Appointment?, doctorsMap: Map<Int, String>){
        val textView = view?.findViewById<TextView>(R.id.nextVisitText)
        if(nearest != null){
            val doctorName = doctorsMap[nearest.doctorId] ?: "Lekarz"
            textView?.text = "Twoja najbliższa wizyta: ${nearest.appointmentDate} o ${nearest.appointmentTime} (dr $doctorName)"
        }else{
            textView?.text = "Brak zaplanowanych wizyt"
        }
    }

    private fun logout(){
        tokenManager.clearToken()
        findNavController().navigate(R.id.loginFragment)
    }
}
