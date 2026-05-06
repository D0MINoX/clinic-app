package com.dominox.clinicapp.ui.screens.adminDashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.TokenManager
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import javax.inject.Inject

@AndroidEntryPoint
class AdminDashboardFragment : Fragment(R.layout.fragment_admin_dashboard) {
    @Inject lateinit var tokenManager: TokenManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.adminWelcomeText).text = "Dzień dobry ${tokenManager.getUserNameFromToken()}!"
    }
}