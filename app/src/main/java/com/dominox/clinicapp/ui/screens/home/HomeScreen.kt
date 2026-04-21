package com.dominox.clinicapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dominox.clinicapp.ui.components.MenuCard
import com.dominox.clinicapp.ui.theme.PrimaryBlue

@Composable
fun HomeScreen(
    onAppointmentsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Klinika Zdrowia",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Witaj!", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Twoja najbliższa wizyta: 24.04.2026 o 10:30 (dr Kowalski)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MenuCard(
                icon = Icons.Default.DateRange,
                title = "Umów wizytę",
                description = "Wybierz dogodny termin konsultacji",
                onClick = onAppointmentsClick
            )
            MenuCard(
                icon = Icons.Default.Schedule,
                title = "Twoje wizyty",
                description = "Sprawdź zaplanowane i zakończone wizyty",
                onClick = onAppointmentsClick
            )
            MenuCard(
                icon = Icons.Default.Description,
                title = "Historia medyczna",
                description = "Przeglądaj dokumentację i wyniki badań",
                onClick = onHistoryClick
            )
            MenuCard(
                icon = Icons.Default.Phone,
                title = "Kontakt",
                description = "Skontaktuj się z recepcją kliniki",
                onClick = onContactClick
            )
            MenuCard(
                icon = Icons.Default.Settings,
                title = "Ustawienia",
                description = "Dostosuj profil i preferencje aplikacji",
                onClick = onSettingsClick
            )
        }
    }
}
