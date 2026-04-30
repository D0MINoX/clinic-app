package com.dominox.clinicapp.ui.screens.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.welcomeText).text = getString(R.string.home_welcome)
        view.findViewById<TextView>(R.id.nextVisitText).text = getString(R.string.home_next_visit)

        val menuRecyclerView = view.findViewById<RecyclerView>(R.id.menuRecyclerView)
        val items = listOf(
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_my_calendar,
                title = getString(R.string.home_menu_book_appointment),
                description = getString(R.string.home_menu_book_appointment_desc),
                destinationId = R.id.bookAppointment
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
    }

    private fun openDialer() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.clinic_phone_uri)))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}
