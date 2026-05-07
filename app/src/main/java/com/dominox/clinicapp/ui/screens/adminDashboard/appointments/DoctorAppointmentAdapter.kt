package com.dominox.clinicapp.ui.screens.adminDashboard.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.data.models.Appointment

class DoctorAppointmentAdapter (private var appointments: List<Appointment>):
    RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder>(){

        private var expandedPosition = -1
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val patientName: TextView = view.findViewById(R.id.patientNameText)
        val time: TextView = view.findViewById(R.id.doctorAppointmentTimeText)
        val reason: TextView = view.findViewById(R.id.doctorAppointmentReasonText)
        val status: TextView = view.findViewById(R.id.doctorAppointmentStatusText)
        val expandedSection: LinearLayout = view.findViewById(R.id.expandedSection)
    }

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolder {
        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.item_doctor_appointment, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        p0: ViewHolder,
        p1: Int
    ) {
        val appointment = appointments[p1]
        p0.patientName.text = "Pacjent ID: ${appointment.patientId}"
        p0.time.text = "Godzina: ${appointment.appointmentTime}"
        p0.reason.text = "Powód: ${appointment.reason}"
        p0.status.text = "Status: ${appointment.status}"

        val isExpanded = p1 == expandedPosition
        p0.expandedSection.visibility = if (isExpanded) View.VISIBLE else View.GONE
        p0.itemView.isActivated = isExpanded

        p0.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else p1
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = appointments.size

}