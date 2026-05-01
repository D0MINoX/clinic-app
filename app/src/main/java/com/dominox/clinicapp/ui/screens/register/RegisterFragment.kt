package com.dominox.clinicapp.ui.screens.register

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AuthService
import com.dominox.clinicapp.data.models.Patient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment: Fragment(R.layout.fragment_register) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.registerToolbar).setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.buttonRegister).setOnClickListener {
            lifecycleScope.launch {
                var isValid = true
                val  name = view.findViewById<TextInputEditText>(R.id.editFirstName).text.toString().trim() + " " +
                        view.findViewById<TextInputEditText>(R.id.editLastName).text.toString().trim()
                val email = view.findViewById<TextInputEditText>(R.id.editEmail).text.toString().trim()
                val phone = view.findViewById<TextInputEditText>(R.id.editPhone).text.toString().trim()
                val dateOfBirth = view.findViewById<TextInputEditText>(R.id.editDateOfBirth).text.toString().trim()
                val address = view.findViewById<TextInputEditText>(R.id.editAddress).text.toString().trim()
                val password = view.findViewById<TextInputEditText>(R.id.editPassword).text.toString().trim()
                val reTypePass = view.findViewById<TextInputEditText>(R.id.editRetypePass).text.toString().trim()

                val emailLayout = view.findViewById<TextInputLayout>(R.id.emailLayout)
                val phoneLayout = view.findViewById<TextInputLayout>(R.id.phoneLayout)
                val passwordLayout = view.findViewById<TextInputLayout>(R.id.passwordLayout)
                val reTypePassLayout = view.findViewById<TextInputLayout>(R.id.reTypePassLayout)

                emailLayout.error = null
                phoneLayout.error = null
                passwordLayout.error = null
                reTypePassLayout.error = null

                if (email.isEmpty()) {
                    emailLayout.error = "Wprowadź adres e-mail"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.error = "Błędny format e-mail"
                    isValid = false
                }

                if (phone.isEmpty()) {
                    phoneLayout.error = "Wprowadź numer telefonu"
                    isValid = false
                }

                if (password.isEmpty()) {
                    passwordLayout.error = "Wprowadź hasło"
                    isValid = false
                }

                if (reTypePass.isEmpty()) {
                    reTypePassLayout.error = "Powtórz hasło"
                    isValid = false
                } else if (password != reTypePass) {
                    reTypePassLayout.error = "Hasła muszą być identyczne"
                    isValid = false
                }

                if (isValid) {
                    val newPatient = Patient(
                        name = name,
                        email = email,
                        phoneNumber = phone,
                        dateOfBirth = dateOfBirth,
                        address = address,
                        passwordHash = password
                    )
                    val result = AuthService().register(newPatient)
                    withContext(Dispatchers.Main) {
                        result.onSuccess {
                            showSimpleAlert("Sukces", "Twoje konto zostało założone! Możesz się teraz zalogować.")
                        }.onFailure { error ->
                            showSimpleAlert("Błąd", "Nie udało się zarejestrować: ${error.message}")
                        }
                    }
                }

                // Funkcja pomocnicza do tworzenia Alertu

            }
        }
    }
    fun showSimpleAlert(title: String, message: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext()) // lub requireContext() we fragmencie
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}