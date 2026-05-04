package com.dominox.clinicapp.ui.screens.appointments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

// SuppressLint zastępuje @RequiresApi — czystsze niż adnotacja na każdej metodzie
@SuppressLint("NewApi")
class BookAppointmentFragment : Fragment(R.layout.fragment_book_appointment) {

    private lateinit var dateField: EditText
    private lateinit var timeField: EditText
    private lateinit var doctorSpinner: Spinner

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    private val doctorList = listOf(
        "— wybierz lekarza —",
        "Jan Kowalski · Neurolog",
        "Anna Nowak · Kardiolog",
        "Piotr Wiśniewski · Ortopeda",
        "Katarzyna Kowalczyk · Dermatolog",
        "Marek Zieliński · Internista",
        "Monika Wójcik · Pediatra",
        "Tomasz Kamiński · Chirurg",
        "Ewa Lewandowska · Okulista"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.bookAppointmentsToolbar)
            .setNavigationOnClickListener { findNavController().navigateUp() }

        doctorSpinner = view.findViewById(R.id.doctorSpinner)
        doctorSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            doctorList
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        dateField = view.findViewById(R.id.dateField)
        timeField = view.findViewById(R.id.timeField)

        dateField.setOnClickListener { openSlotPickerDialog() }
        timeField.setOnClickListener { openSlotPickerDialog() }

        view.findViewById<MaterialButton>(R.id.bookCancelButton).setOnClickListener {
            findNavController().navigateUp()
            Snackbar.make(view, "Anulowano rezerwację", Snackbar.LENGTH_SHORT).show()
        }

        view.findViewById<MaterialButton>(R.id.bookReserveButton).setOnClickListener {
            onReserveClicked(view)
        }
    }

    private fun openSlotPickerDialog() {
        if (doctorSpinner.selectedItemPosition == 0) {
            Snackbar.make(requireView(), "Najpierw wybierz lekarza", Snackbar.LENGTH_SHORT).show()
            return
        }

        val doctorName = doctorSpinner.selectedItem.toString()

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_date_slot_picker, null)

        val tvDoctorName = dialogView.findViewById<TextView>(R.id.dialogDoctorName)
        val tvDate       = dialogView.findViewById<TextView>(R.id.tvSelectedDate)
        val btnPrev      = dialogView.findViewById<ImageButton>(R.id.btnPrevDay)
        val btnNext      = dialogView.findViewById<ImageButton>(R.id.btnNextDay)
        val chipGroup    = dialogView.findViewById<ChipGroup>(R.id.chipGroupSlots)
        val tvNoSlots    = dialogView.findViewById<TextView>(R.id.tvNoSlots)
        val btnCancel    = dialogView.findViewById<MaterialButton>(R.id.btnDialogCancel)
        val btnConfirm   = dialogView.findViewById<MaterialButton>(R.id.btnDialogConfirm)

        tvDoctorName.text = doctorName

        val today = LocalDate.now()
        var currentDate = LocalDate.now()
        var pickedSlot: String? = null

        fun formatDate(date: LocalDate): String {
            val months = arrayOf(
                "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
                "lipca", "sierpnia", "września", "października", "listopada", "grudnia"
            )
            val dayName = when (date.dayOfWeek) {
                DayOfWeek.MONDAY    -> "Poniedziałek"
                DayOfWeek.TUESDAY   -> "Wtorek"
                DayOfWeek.WEDNESDAY -> "Środa"
                DayOfWeek.THURSDAY  -> "Czwartek"
                DayOfWeek.FRIDAY    -> "Piątek"
                DayOfWeek.SATURDAY  -> "Sobota"
                DayOfWeek.SUNDAY    -> "Niedziela"
                else                -> ""
            }
            val prefix = if (date == today) "Dzisiaj" else dayName
            return "$prefix, ${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year}"
        }

        fun refreshSlots(date: LocalDate) {
            tvDate.text = formatDate(date)

            btnPrev.isEnabled = date.isAfter(today)
            btnPrev.alpha     = if (date.isAfter(today)) 1f else 0.3f

            chipGroup.removeAllViews()
            pickedSlot = null

            if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                tvNoSlots.text       = "Niedziela — gabinet nieczynny"
                tvNoSlots.visibility = View.VISIBLE
                chipGroup.visibility = View.GONE
                return
            }

            // Docelowo: GET /api/doctors/{id}/availability?date={date}
            val slots = generateAvailableSlots(date, occupiedSlots = emptyList())

            if (slots.isEmpty()) {
                tvNoSlots.text = "Brak wolnych terminów w tym dniu"
                tvNoSlots.visibility = View.VISIBLE
                chipGroup.visibility = View.GONE
            } else {
                tvNoSlots.visibility = View.GONE
                chipGroup.visibility = View.VISIBLE
                slots.forEach { slot ->
                    val chip = Chip(requireContext()).apply {
                        text = slot
                        isCheckable = true
                    }
                    chipGroup.addView(chip)
                }
            }
        }

        refreshSlots(currentDate)

        btnPrev.setOnClickListener {
            if (currentDate.isAfter(today)) {
                currentDate = currentDate.minusDays(1)
                refreshSlots(currentDate)
            }
        }

        btnNext.setOnClickListener {
            currentDate = currentDate.plusDays(1)
            refreshSlots(currentDate)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            pickedSlot = if (checkedIds.isNotEmpty())
                group.findViewById<Chip>(checkedIds[0])?.text?.toString()
            else null
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            if (pickedSlot == null) {
                Snackbar.make(dialogView, "Wybierz godzinę wizyty", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectedDate = currentDate.toString()
            selectedTime = pickedSlot!!
            dateField.setText(selectedDate)
            timeField.setText(selectedTime)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun onReserveClicked(view: View) {
        if (doctorSpinner.selectedItemPosition == 0) {
            Snackbar.make(view, "Wybierz lekarza", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Snackbar.make(view, "Wybierz datę i godzinę wizyty", Snackbar.LENGTH_SHORT).show()
            return
        }

        // TODO: POST /api/appointments
        Snackbar.make(view, "Zarezerwowano: $selectedDate o $selectedTime", Snackbar.LENGTH_LONG).show()
    }

    private fun generateAvailableSlots(date: LocalDate, occupiedSlots: List<String>): List<String> {
        val isSaturday = date.dayOfWeek == DayOfWeek.SATURDAY
        val start = LocalTime.of(8, 0)
        val end = if (isSaturday) LocalTime.of(12, 0) else LocalTime.of(17, 0)

        val slots = mutableListOf<String>()
        var current = start
        while (current.isBefore(end)) {
            val slotKey = "${date}T${current}"
            if (!occupiedSlots.contains(slotKey)) {
                slots.add(current.toString())
            }
            current = current.plusMinutes(30)
        }
        return slots
    }
}