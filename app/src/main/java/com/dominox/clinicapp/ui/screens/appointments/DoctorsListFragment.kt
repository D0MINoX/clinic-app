package com.dominox.clinicapp.ui.screens.appointments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.DoctorService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DoctorsListFragment: Fragment(R.layout.fragment_doctor_list) {

    @Inject
    lateinit var doctorService: DoctorService
    private val doctorsAdapter = DoctorsListAdapter(emptyList()) { doctor ->
        val bundle = Bundle().apply {
            putInt("doctorId", doctor.id)
            putString("doctorName", "${doctor.firstName} ${doctor.lastName}")
            putString("doctorSpecialization", doctor.specialization)
        }
        findNavController().navigate(R.id.action_doctorsListFragment_to_doctorDetailsFragment, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(view)
        setupRecyclerView(view)
        loadDoctors()
    }

    private fun setupToolbar(view: View) {
        view.findViewById<MaterialToolbar>(R.id.bookAppointmentsToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView(view: View) {
        view.findViewById<RecyclerView>(R.id.doctorsRecyclerView).adapter = doctorsAdapter
    }

    private fun loadDoctors() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = doctorService.getDoctors()
            result.onSuccess { doctors ->
                doctorsAdapter.updateData(doctors)
                if (doctors.isEmpty()) {
                    Snackbar.make(requireView(), "Brak dostępnych lekarzy", Snackbar.LENGTH_SHORT).show()
                }
            }.onFailure {
                Snackbar.make(requireView(), "Nie udało się pobrać listy lekarzy", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}