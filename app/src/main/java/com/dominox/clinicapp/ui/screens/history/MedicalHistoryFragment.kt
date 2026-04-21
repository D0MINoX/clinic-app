package com.dominox.clinicapp.ui.screens.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.appbar.MaterialToolbar

class MedicalHistoryFragment : Fragment(R.layout.fragment_medical_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.historyToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}
