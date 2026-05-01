package com.dominox.clinicapp.ui.screens.login

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.R
import com.dominox.clinicapp.api.AuthService
import com.dominox.clinicapp.data.models.LoginRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
                lifecycleScope.launch {
                    val request = LoginRequest(
                        email = email,
                        passwordHash = password
                    )

                    val result = AuthService().login(request)

                    withContext(Dispatchers.Main) {
                        result.onSuccess {
                            showSimpleAlert("Sukces", "Zalogowano pomyślnie")
                        }.onFailure {
                            showSimpleAlert("Logowanie nieudane", it.message ?: "Spróbuj ponownie")
                        }
                    }
                }
                findNavController().navigate(R.id.homeFragment)
            }
        }

        view.findViewById<Button>(R.id.buttonRegister).setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
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