package com.example.e_posyandu.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material3 Shape System untuk E-POSYANDU
 * 
 * Shapes digunakan untuk menciptakan hierarchy dan konsistensi visual
 * - extraSmall: Components kecil seperti chips, badges
 * - small: Buttons, text fields
 * - medium: Cards, dialogs
 * - large: Bottom sheets, navigation drawer
 * - extraLarge: FAB, hero components
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
