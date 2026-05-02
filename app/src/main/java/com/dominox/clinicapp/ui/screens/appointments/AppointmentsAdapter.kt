package com.dominox.clinicapp.ui.screens.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.data.models.Appointment

class AppointmentsAdapter(private var appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    // Klasa trzymająca referencje do widoków w item_appointment.xml
    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorText: TextView = view.findViewById(R.id.doctorNameText)
        val dateText: TextView = view.findViewById(R.id.appointmentDateText)
        val statusText: TextView = view.findViewById(R.id.appointmentStatusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        // Ponieważ w JSONie masz tylko doctorId, na razie wyświetlimy ID.
        // Docelowo serwer powinien przesyłać też imię lekarza.
        holder.doctorText.text = "Lekarz ID: ${appointment.doctorId}"

        // Łączymy datę z godziną
        holder.dateText.text = "${appointment.appointmentDate} o ${appointment.appointmentTime}"

        holder.statusText.text = "Status: ${appointment.status}"
    }

    override fun getItemCount() = appointments.size

    // Funkcja do odświeżania danych
    fun updateData(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }
}