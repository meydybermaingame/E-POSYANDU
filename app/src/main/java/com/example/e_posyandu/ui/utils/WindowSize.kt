package com.example.e_posyandu.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class holding responsive values that adapt to screen size and orientation
 */
data class ResponsiveValues(
    val headerHeight: Dp,
    val titleFontSize: Int,
    val subtitleFontSize: Int,
    val iconSize: Dp,
    val horizontalPadding: Dp,
    val cardSpacing: Dp,
    val isLandscape: Boolean,
    val isTablet: Boolean
)

/**
 * Composable function that returns responsive values based on current screen configuration
 * 
 * Screen size breakpoints:
 * - Compact: < 600dp (Phone Portrait)
 * - Medium: 600dp - 840dp (Tablet Portrait, Phone Landscape)
 * - Expanded: > 840dp (Tablet Landscape)
 */
@Composable
fun rememberResponsiveValues(): ResponsiveValues {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    return when {
        // Compact: Phone Portrait
        screenWidth < 600.dp && !isLandscape -> ResponsiveValues(
            headerHeight = 64.dp,
            titleFontSize = 20,
            subtitleFontSize = 12,
            iconSize = 24.dp,
            horizontalPadding = 16.dp,
            cardSpacing = 12.dp,
            isLandscape = false,
            isTablet = false
        )
        
        // Medium: Tablet Portrait or Phone Landscape
        screenWidth in 600.dp..840.dp || (isLandscape && screenWidth < 840.dp) -> ResponsiveValues(
            headerHeight = 72.dp,
            titleFontSize = 22,
            subtitleFontSize = 14,
            iconSize = 28.dp,
            horizontalPadding = 24.dp,
            cardSpacing = 16.dp,
            isLandscape = isLandscape,
            isTablet = screenWidth >= 600.dp
        )
        
        // Expanded: Tablet Landscape
        else -> ResponsiveValues(
            headerHeight = 80.dp,
            titleFontSize = 24,
            subtitleFontSize = 16,
            iconSize = 32.dp,
            horizontalPadding = 32.dp,
            cardSpacing = 20.dp,
            isLandscape = true,
            isTablet = true
        )
    }
}
