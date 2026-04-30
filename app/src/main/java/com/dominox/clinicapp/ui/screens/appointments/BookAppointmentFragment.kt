package com.dominox.clinicapp.ui.screens.appointments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.app.DatePickerDialog
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentFragment : Fragment(R.layout.fragment_book_appointment) {

    private lateinit var dateField: EditText
    private var selectedDate: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // przycisk do cofnięcia
        view.findViewById<MaterialToolbar>(R.id.bookAppointmentsToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // datePicker
        dateField = view.findViewById(R.id.dateField)
        dateField.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Formatuj wybraną datę
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,  // miesiące są od 0
                    selectedDay
                )

                // Wyświetl datę w polu
                dateField.setText(formattedDate)
                selectedDate = formattedDate

                // wyświetlamy wiadomość
                Snackbar.make(
                    dateField,
                    "Wybrana data: $formattedDate",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            year,
            month,
            day
        )

        // blokujemy przeszłe daty
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()
    }
}