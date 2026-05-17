package com.dominox.clinicapp.ui.screens.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.data.models.Appointment

class AppointmentsAdapter(
    private var appointments: List<Appointment>,
    private var doctorsMap: Map<Int, String>
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    // Śledzimy pozycję rozwiniętego elementu (-1 oznacza brak)
    private var expandedPosition = -1

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorText: TextView = view.findViewById(R.id.doctorNameText)
        val dateText: TextView = view.findViewById(R.id.appointmentDateText)
        val statusText: TextView = view.findViewById(R.id.appointmentStatusText)

        // Nowe widoki sekcji rozwijanej
        val expandedSection: LinearLayout = view.findViewById(R.id.expandedSection)
        val recommendationsText: TextView = view.findViewById(R.id.recommendationsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        val doctorName = doctorsMap[appointment.doctorId] ?: "Nieznany"

        holder.doctorText.text = "Lekarz: $doctorName"
        holder.dateText.text = "${appointment.appointmentDate} o ${appointment.appointmentTime}"
        holder.statusText.text = "Status: ${appointment.status}"

        // Ustawianie tekstu zaleceń (jeśli są puste, dajemy komunikat)
        holder.recommendationsText.text = if (appointment.recommendations.isNullOrBlank()) {
            "Brak zaleceń dla tej wizyty."
        } else {
            appointment.recommendations
        }

        // Logika rozwijania/zwijania
        val isExpanded = position == expandedPosition
        holder.expandedSection.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded

        holder.itemView.setOnClickListener {
            // Jeśli klikniemy w już rozwinięty -> zwiń (-1). W przeciwnym razie rozwiń nową pozycję.
            expandedPosition = if (isExpanded) -1 else position
            notifyDataSetChanged() // Odśwież listę, by zastosować zmiany widoczności
        }
    }

    override fun getItemCount() = appointments.size

    fun updateData(newList: List<Appointment>, newMap: Map<Int, String>) {
        appointments = newList
        doctorsMap = newMap
        expandedPosition = -1 // Resetujemy pozycję przy odświeżaniu listy
        notifyDataSetChanged()
    }
}