package com.dominox.clinicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dominox.clinicapp.navigation.ClinicNavigation
import com.dominox.clinicapp.ui.theme.ClinicAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClinicAppTheme {
                ClinicNavigation()
            }
        }
    }
}
