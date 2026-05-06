package com.dominox.clinicapp.ui.screens.adminDashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.TokenManager
import com.dominox.clinicapp.ui.screens.home.HomeMenuAdapter
import com.dominox.clinicapp.ui.screens.home.HomeMenuItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import javax.inject.Inject

@AndroidEntryPoint
class AdminDashboardFragment : Fragment(R.layout.fragment_admin_dashboard) {
    @Inject lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.adminWelcomeText).text = "Dzień dobry, ${tokenManager.getUserNameFromToken()?.substringBefore(" ")}!"

        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardRecyclerView)
        val recyclerItems = listOf(
            HomeMenuItem(
                iconResId = android.R.drawable.ic_menu_agenda,
                title = "Wizyty",
                description = "Sprawdź zaplanowane wizyty",
                destinationId = R.id.doctorAppointmentsFragment
            )
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HomeMenuAdapter(recyclerItems){ item ->
            if(item.destinationId != null){
                findNavController().navigate(item.destinationId)
            }
        }

        //wylogowanie
        view.findViewById<MaterialButton>(R.id.dashboardLogoutButton).setOnClickListener {
            logout()
        }
    }

    private fun logout(){
        tokenManager.clearToken()
        findNavController().navigate(R.id.loginFragment)
    }
}