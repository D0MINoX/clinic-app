package com.dominox.clinicapp.ui.screens.appointments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

class AppointmentsFragment : Fragment(R.layout.fragment_appointments) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.appointmentsToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.bookAppointmentButton).setOnClickListener {
            Snackbar.make(view, R.string.appointments_book_action_placeholder, Snackbar.LENGTH_SHORT).show()
        }
    }
}
