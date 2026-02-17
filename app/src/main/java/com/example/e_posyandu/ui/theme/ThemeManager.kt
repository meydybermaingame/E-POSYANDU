package com.example.e_posyandu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * ThemeManager — mengelola state dark/light mode secara global.
 * State ini akan bertahan selama lifecycle aplikasi berjalan.
 */
class ThemeManager {
    var isDarkMode by mutableStateOf(false)
        private set

    fun toggleTheme() {
        isDarkMode = !isDarkMode
    }
}

/**
 * CompositionLocal agar ThemeManager bisa diakses dari composable manapun
 * tanpa perlu passing parameter secara manual.
 */
val LocalThemeManager = compositionLocalOf { ThemeManager() }
