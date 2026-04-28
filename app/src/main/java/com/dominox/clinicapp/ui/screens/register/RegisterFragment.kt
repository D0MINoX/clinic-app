package com.dominox.clinicapp.ui.screens.register

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment: Fragment(R.layout.fragment_register) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonRegister).setOnClickListener {
            var isValid = true

            val email = view.findViewById<TextInputEditText>(R.id.editEmail).text.toString().trim()
            val phone = view.findViewById<TextInputEditText>(R.id.editPhone).text.toString().trim()
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
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }
}