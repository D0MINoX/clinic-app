package com.dominox.clinicapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.dominox.clinicapp.api.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val token = tokenManager.getToken()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController

        if (token != null) {


            navController.navigate(R.id.homeFragment)
        } else {
            navController.navigate(R.id.loginFragment)
        }
    }
}
