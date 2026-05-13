package com.dominox.clinicapp.ui.screens.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.data.models.Doctor

class DoctorsListAdapter(
    private var doctors: List<Doctor>,
    private val onDoctorClick: (Doctor) -> Unit
): RecyclerView.Adapter<DoctorsListAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val doctorNameText: TextView = view.findViewById(R.id.doctorNameText)
        val doctorSpecializationText: TextView = view.findViewById(R.id.doctorSpecialtyText)
        val doctorImage: ImageView = view.findViewById(R.id.doctorImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.doctorNameText.text = "${doctor.firstName} ${doctor.lastName}"
        holder.doctorSpecializationText.text = doctor.specialization ?: "Brak specjalizacji"
        holder.doctorImage.setImageResource(R.drawable.dariusz)
        
        holder.itemView.setOnClickListener {
            onDoctorClick(doctor)
        }
    }

    override fun getItemCount() = doctors.size

    fun updateData(newList: List<Doctor>) {
        doctors = newList
        notifyDataSetChanged()
    }
}