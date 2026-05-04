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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.api.DoctorService
import com.dominox.clinicapp.api.TokenManager
import com.dominox.clinicapp.data.models.AppointmentRequest
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

// SuppressLint zastępuje @RequiresApi — czystsze niż adnotacja na każdej metodzie
@SuppressLint("NewApi")
@AndroidEntryPoint
class BookAppointmentFragment : Fragment(R.layout.fragment_book_appointment) {
    @Inject
    lateinit var doctorService : DoctorService
    @Inject lateinit var appointmentService: AppointmentService
    @Inject lateinit var tokenManager: TokenManager
    private lateinit var dateTimeField: EditText
    private lateinit var reasonField: EditText
    private lateinit var doctorSpinner: Spinner
    private var fullDoctorList = listOf<com.dominox.clinicapp.data.models.Doctor>()
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private val doctorList = mutableListOf<String>("Wybierz lekarza")

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
        reasonField = view.findViewById(R.id.reasonEditText)
        dateTimeField = view.findViewById(R.id.dateTimeField)
        dateTimeField.setOnClickListener { openSlotPickerDialog() }

        view.findViewById<MaterialButton>(R.id.bookCancelButton).setOnClickListener {
            findNavController().navigateUp()
            Snackbar.make(view, "Anulowano rezerwację", Snackbar.LENGTH_SHORT).show()
        }

        view.findViewById<MaterialButton>(R.id.bookReserveButton).setOnClickListener {
            onReserveClicked(view)
        }
        fetchDoctors()
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
            btnPrev.alpha = if (date.isAfter(today)) 1f else 0.3f

            chipGroup.removeAllViews()
            pickedSlot = null

            if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                tvNoSlots.text = "Niedziela — gabinet nieczynny"
                tvNoSlots.visibility = View.VISIBLE
                chipGroup.visibility = View.GONE
                return
            }

            // Pobieramy ID wybranego lekarza (pozycja - 1, bo na 0 jest "Wybierz lekarza")
            val selectedDoctor = fullDoctorList.getOrNull(doctorSpinner.selectedItemPosition - 1)
            if (selectedDoctor == null) return

            // WYWOŁANIE API
            viewLifecycleOwner.lifecycleScope.launch {
                tvNoSlots.text = "Pobieranie wolnych terminów..."
                tvNoSlots.visibility = View.VISIBLE
                chipGroup.visibility = View.GONE

                val result = appointmentService.getOccupiedSlots(selectedDoctor.id.toInt(), date.toString())

                val occupied = result.getOrDefault(emptyList())
                val slots = generateAvailableSlots(date, occupiedSlots = occupied)

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
            dateTimeField.setText(selectedDate + " | " + selectedTime)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun onReserveClicked(view: View) {
        // 1. Walidacja wyboru lekarza
        val selectedDoctor = fullDoctorList.getOrNull(doctorSpinner.selectedItemPosition - 1)
        if (selectedDoctor == null) {
            Snackbar.make(view, "Wybierz lekarza", Snackbar.LENGTH_SHORT).show()
            return
        }

        // 2. Walidacja daty i czasu
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Snackbar.make(view, "Wybierz datę i godzinę wizyty", Snackbar.LENGTH_SHORT).show()
            return
        }

        val patientId = tokenManager.getUserIdFromToken()

        if (patientId == null) {
            Snackbar.make(view, "Błąd: Nie znaleziono ID użytkownika. Zaloguj się ponownie.", Snackbar.LENGTH_LONG).show()
            return
        }
        val userReason = reasonField.text.toString().trim()
        val finalReason = if (userReason.isEmpty()) "Wizyta kontrolna" else userReason
        val request = AppointmentRequest(
            patientId = patientId,
            doctorId = selectedDoctor.id.toInt(),
            appointmentDate = selectedDate,
            appointmentTime = selectedTime,
            reason = finalReason
        )

        // 4. Wywołanie API w Coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.bookAppointment(request)

            if (result.isSuccess) {
                val booked = result.getOrNull()
                Snackbar.make(view, "Sukces! Umówiono wizytę nr ${booked?.id}", Snackbar.LENGTH_LONG).show()
                view.postDelayed({ findNavController().navigateUp() }, 2000)
            } else {
                val error = result.exceptionOrNull()
                Snackbar.make(view, "Błąd rezerwacji: ${error?.message}", Snackbar.LENGTH_LONG).show()
            }
        }
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
    private fun fetchDoctors() {

        viewLifecycleOwner.lifecycleScope.launch {
            val result = doctorService.getDoctors()

            if (result.isSuccess) {
                val doctors = result.getOrNull() ?: emptyList()
                fullDoctorList = result.getOrNull() ?: emptyList()
                doctorList.clear()
                doctorList.add("Wybierz lekarza")
                doctorList.addAll(doctors.map { "${it.firstName} ${it.lastName} - ${it.specialization}" })

                // Powiadamiamy adapter o zmianie danych
                (doctorSpinner.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
            } else {
                val error = result.exceptionOrNull()
                Snackbar.make(requireView(), "Błąd pobierania lekarzy: ${error?.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}