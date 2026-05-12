package com.dominox.clinicapp.ui.screens.appointments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
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
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@SuppressLint("NewApi")
@AndroidEntryPoint
class DoctorDetailsFragment : Fragment(R.layout.fragment_doctor_details) {

    @Inject lateinit var appointmentService: AppointmentService
    @Inject lateinit var tokenManager: TokenManager
    
    private var doctorId: Int = -1
    private var doctorName: String = ""
    private var doctorSpecialization: String? = null
    
    private lateinit var tvDate: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var chipGroup: ChipGroup
    private lateinit var tvNoSlots: TextView
    private lateinit var reasonField: EditText
    
    private var selectedDate: LocalDate = LocalDate.now()
    private var pickedSlot: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            doctorId = it.getInt("doctorId")
            doctorName = it.getString("doctorName") ?: ""
            doctorSpecialization = it.getString("doctorSpecialization")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
        refreshSlots(selectedDate)
    }

    private fun setupUI(view: View) {
        view.findViewById<MaterialToolbar>(R.id.doctorDetailsToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<TextView>(R.id.doctorDetailName).text = doctorName
        view.findViewById<TextView>(R.id.doctorDetailSpecialty).text = doctorSpecialization ?: "Brak specjalizacji"

        tvDate = view.findViewById(R.id.tvSelectedDate)
        btnPrev = view.findViewById(R.id.btnPrevDay)
        btnNext = view.findViewById(R.id.btnNextDay)
        chipGroup = view.findViewById(R.id.chipGroupSlots)
        tvNoSlots = view.findViewById(R.id.tvNoSlots)
        reasonField = view.findViewById(R.id.doctorDetailReasonEditText)

        btnPrev.setOnClickListener {
            if (selectedDate.isAfter(LocalDate.now())) {
                selectedDate = selectedDate.minusDays(1)
                refreshSlots(selectedDate)
            }
        }

        btnNext.setOnClickListener {
            selectedDate = selectedDate.plusDays(1)
            refreshSlots(selectedDate)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            pickedSlot = if (checkedIds.isNotEmpty())
                group.findViewById<Chip>(checkedIds[0])?.text?.toString()
            else null
        }

        view.findViewById<MaterialButton>(R.id.doctorDetailCancelButton).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<MaterialButton>(R.id.doctorDetailReserveButton).setOnClickListener {
            onReserveClicked(view)
        }
    }

    private fun formatDate(date: LocalDate): String {
        val today = LocalDate.now()
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

    private fun refreshSlots(date: LocalDate) {
        val today = LocalDate.now()
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

        viewLifecycleOwner.lifecycleScope.launch {
            tvNoSlots.text = "Pobieranie wolnych terminów..."
            tvNoSlots.visibility = View.VISIBLE
            chipGroup.visibility = View.GONE

            val result = appointmentService.getOccupiedSlots(doctorId, date.toString())

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

    private fun onReserveClicked(view: View) {
        if (pickedSlot == null) {
            Snackbar.make(view, "Wybierz godzinę wizyty", Snackbar.LENGTH_SHORT).show()
            return
        }

        val patientId = tokenManager.getUserIdFromToken()
        if (patientId == null) {
            Snackbar.make(view, "Błąd: Nie znaleziono ID użytkownika", Snackbar.LENGTH_LONG).show()
            return
        }

        val userReason = reasonField.text.toString().trim()
        val finalReason = if (userReason.isEmpty()) "Wizyta kontrolna" else userReason
        
        val request = AppointmentRequest(
            patientId = patientId,
            doctorId = doctorId,
            appointmentDate = selectedDate.toString(),
            appointmentTime = pickedSlot!!,
            reason = finalReason
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val result = appointmentService.bookAppointment(request)
            if (result.isSuccess) {
                Snackbar.make(view, "Sukces! Umówiono wizytę", Snackbar.LENGTH_LONG).show()
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
            if (!occupiedSlots.contains(slotKey) && date.atTime(current).isAfter(LocalDateTime.now())) {
                slots.add(current.toString())
            }
            current = current.plusMinutes(30)
        }
        return slots
    }
}
