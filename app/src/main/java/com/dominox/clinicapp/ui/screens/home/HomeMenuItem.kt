package com.dominox.clinicapp.ui.screens.home

import androidx.annotation.DrawableRes

data class HomeMenuItem(
    @DrawableRes val iconResId: Int,
    val title: String,
    val description: String,
    val destinationId: Int? = null,
    val isContactAction: Boolean = false
)
