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
    val gridColumns: Int,        // Grid columns: 2 for portrait, 4 for landscape
    val menuItemHeight: Dp,      // Menu card height: 160dp portrait, 120dp landscape
    val chartHeight: Dp,         // Chart height: 500dp portrait, 400dp landscape
    val isLandscape: Boolean,
    val isTablet: Boolean,
    // Additional adaptive properties
    val contentPadding: Dp,      // Inner content padding
    val buttonHeight: Dp,        // Standard button height
    val topBarTitleSize: Int,    // TopAppBar title font size
    val bodyFontSize: Int,       // Body text font size
    val contentMaxWidth: Dp,     // Max width for form/content on large screens
    val navIconSize: Dp,         // Bottom nav icon size
    val navFontSize: Int,        // Bottom nav label font size
    val smallIconSize: Dp,       // Small icon (status indicators etc.)
    val cardCornerRadius: Dp     // Card corner radius
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
            gridColumns = 2,
            menuItemHeight = 160.dp,
            chartHeight = 500.dp,
            isLandscape = false,
            isTablet = false,
            contentPadding = 16.dp,
            buttonHeight = 48.dp,
            topBarTitleSize = 18,
            bodyFontSize = 14,
            contentMaxWidth = Dp.Unspecified,
            navIconSize = 24.dp,
            navFontSize = 10,
            smallIconSize = 20.dp,
            cardCornerRadius = 12.dp
        )
        
        // Medium: Tablet Portrait or Phone Landscape
        screenWidth in 600.dp..840.dp || (isLandscape && screenWidth < 840.dp) -> ResponsiveValues(
            headerHeight = 72.dp,
            titleFontSize = 22,
            subtitleFontSize = 14,
            iconSize = 28.dp,
            horizontalPadding = 24.dp,
            cardSpacing = 16.dp,
            gridColumns = if (isLandscape) 4 else 2,
            menuItemHeight = if (isLandscape) 120.dp else 160.dp,
            chartHeight = if (isLandscape) 400.dp else 500.dp,
            isLandscape = isLandscape,
            isTablet = screenWidth >= 600.dp,
            contentPadding = 24.dp,
            buttonHeight = 52.dp,
            topBarTitleSize = 20,
            bodyFontSize = 15,
            contentMaxWidth = 600.dp,
            navIconSize = 26.dp,
            navFontSize = 11,
            smallIconSize = 22.dp,
            cardCornerRadius = 16.dp
        )
        
        // Expanded: Tablet Landscape
        else -> ResponsiveValues(
            headerHeight = 80.dp,
            titleFontSize = 24,
            subtitleFontSize = 16,
            iconSize = 32.dp,
            horizontalPadding = 32.dp,
            cardSpacing = 20.dp,
            gridColumns = 4,
            menuItemHeight = 140.dp,
            chartHeight = 450.dp,
            isLandscape = true,
            isTablet = true,
            contentPadding = 32.dp,
            buttonHeight = 56.dp,
            topBarTitleSize = 22,
            bodyFontSize = 16,
            contentMaxWidth = 800.dp,
            navIconSize = 28.dp,
            navFontSize = 12,
            smallIconSize = 24.dp,
            cardCornerRadius = 20.dp
        )
    }
}

