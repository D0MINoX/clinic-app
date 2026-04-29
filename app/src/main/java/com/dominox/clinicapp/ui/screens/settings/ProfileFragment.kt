package com.dominox.clinicapp.ui.screens.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var currentPasswordInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var phoneLayout: TextInputLayout
    private lateinit var currentPasswordLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        setupListeners()

        loadUserProfile()
    }

    private fun initializeViews(view: View) {
        firstNameInput = view.findViewById(R.id.firstNameInput)
        lastNameInput = view.findViewById(R.id.lastNameInput)
        emailInput = view.findViewById(R.id.emailInput)
        phoneInput = view.findViewById(R.id.phoneInput)
        currentPasswordInput = view.findViewById(R.id.currentPasswordInput)
        newPasswordInput = view.findViewById(R.id.newPasswordInput)
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput)
        saveButton = view.findViewById(R.id.saveButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        firstNameLayout = view.findViewById(R.id.firstNameLayout)
        lastNameLayout = view.findViewById(R.id.lastNameLayout)
        emailLayout = view.findViewById(R.id.emailLayout)
        phoneLayout = view.findViewById(R.id.phoneLayout)

        currentPasswordLayout = view.findViewById(R.id.currentPasswordLayout)
        newPasswordLayout = view.findViewById(R.id.newPasswordLayout)
        confirmPasswordLayout = view.findViewById(R.id.confirmPasswordLayout)


        // Ustawienie toolbara
        view.findViewById<MaterialToolbar>(R.id.profileToolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            saveProfile()
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadUserProfile() {
        // TODO: Wczytać dane z bazy
        firstNameInput.setText("Mariola")
        lastNameInput.setText("Żbik")
        emailInput.setText("abc@abc.abc")
        phoneInput.setText("123456789")
    }

    private fun saveProfile() {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val currentPassword = currentPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        firstNameLayout.error = null
        lastNameLayout.error = null
        emailLayout.error = null
        phoneLayout.error = null
        currentPasswordLayout.error = null
        newPasswordLayout.error = null

        if(firstName.isEmpty()){
            firstNameLayout.error = "Podaj imię"
        }
        if(lastName.isEmpty()){
            lastNameLayout.error = "Podaj nazwisko"
        }
        if(email.isEmpty()){
            emailLayout.error = "Podaj email"
        }
        if(phone.isEmpty()){
            phoneLayout.error = "Podaj numer telefonu"
        }
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showError("Wszystkie pola są wymagane")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Nieprawidłowy format email"
            return
        }

        // Zmiana hasła
        if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
            if (newPassword.isEmpty()) {
                newPasswordLayout.error = "Podaj nowe hasło"
                return
            }

            if (newPassword != confirmPassword) {
                confirmPasswordLayout.error = "Hasła nie są zgodne"
                return
            }

            if (currentPassword.isEmpty()) {
                currentPasswordLayout.error = "Podaj aktualne hasło"
                return
            }

            // TODO: Walidacja w bazie
        }

        // TODO: Zapisać dane w bazie
        showSuccess(getString(R.string.profile_save_success))

        findNavController().navigateUp()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}


