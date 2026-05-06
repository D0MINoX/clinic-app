package com.dominox.clinicapp.ui.screens.adminDashboard.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AppointmentService
import com.dominox.clinicapp.ui.screens.appointments.AppointmentsAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.coroutines.coroutineContext

class DoctorDayGroupAdapter(
    private val groups: List<DoctorAppointmentsFragment.DayGroup>
): RecyclerView.Adapter<DoctorDayGroupAdapter.ViewHolder>(){

    private val today = LocalDate.now()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val button: MaterialButton = view.findViewById(R.id.dayHeaderButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.dayAppointmentsRecyclerView)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater
            .from(p0.context)
            .inflate(R.layout.item_doctor_day_group, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val group = groups[p1]


        p0.button.text = formatDate(LocalDate.parse(group.date))

        //wewnetrzna lista
        p0.recyclerView.layoutManager = LinearLayoutManager(p0.itemView.context)
        p0.recyclerView.adapter = AppointmentsAdapter(group.appointments)

        //rozwijanie
        p0.recyclerView.visibility = if(group.isExpanded) View.VISIBLE else View.GONE
        p0.button.setIconResource(if (group.isExpanded) R.drawable.ic_remove_24 else R.drawable.ic_add_24)

        p0.button.setOnClickListener {
            group.isExpanded = !group.isExpanded
            notifyItemChanged(p1)
        }
    }

    override fun getItemCount() = groups.size

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
}