package com.dominox.clinicapp.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dominox.clinicapp.ui.screens.appointments.AppointmentsScreen
import com.dominox.clinicapp.ui.screens.history.MedicalHistoryScreen
import com.dominox.clinicapp.ui.screens.home.HomeScreen
import com.dominox.clinicapp.ui.screens.settings.SettingsScreen

object ClinicRoute {
    const val HOME = "home"
    const val APPOINTMENTS = "appointments"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

@Composable
fun ClinicNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ClinicRoute.HOME
    ) {
        composable(ClinicRoute.HOME) {
            val context = LocalContext.current
            HomeScreen(
                onAppointmentsClick = { navController.navigate(ClinicRoute.APPOINTMENTS) },
                onHistoryClick = { navController.navigate(ClinicRoute.HISTORY) },
                onSettingsClick = { navController.navigate(ClinicRoute.SETTINGS) },
                onContactClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+48123456789"))
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                }
            )
        }
        composable(ClinicRoute.APPOINTMENTS) {
            AppointmentsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(ClinicRoute.HISTORY) {
            MedicalHistoryScreen(onBackClick = { navController.popBackStack() })
        }
        composable(ClinicRoute.SETTINGS) {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
