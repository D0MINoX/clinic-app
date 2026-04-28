package com.dominox.clinicapp.ui.screens.login

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            val email = view.findViewById<TextInputEditText>(R.id.editEmail).text.toString().trim()
            val password = view.findViewById<TextInputEditText>(R.id.editPassword).text.toString().trim()

            val emailLayout = view.findViewById<TextInputLayout>(R.id.emailLayout)
            val passwordLayout = view.findViewById<TextInputLayout>(R.id.passwordLayout)

            emailLayout.error = null
            passwordLayout.error = null

            var isValid = true

            if (email.isEmpty()){
                emailLayout.error = "Podaj email"
                isValid = false
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailLayout.error = "Nieprawidłowy email"
                isValid = false
            }

            if (password.isEmpty()){
                passwordLayout.error = "Podaj hasło"
                isValid = false
            }

            if (isValid){
                Toast.makeText(requireContext(), "Logowanie OK", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.homeFragment)
            }
        }

        view.findViewById<Button>(R.id.buttonRegister).setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }
}