package com.example.e_posyandu.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Dark Color Scheme
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = Color(0xFF003920),
    primaryContainer = PrimaryBase,
    onPrimaryContainer = SecondaryPalette,
    
    secondary = PrimaryBase,
    onSecondary = SecondaryPalette,
    secondaryContainer = PrimaryBase.copy(alpha = 0.7f),
    onSecondaryContainer = PrimaryAccent,
    
    tertiary = ComplementaryAccent,
    onTertiary = Color(0xFF1E282D),
    tertiaryContainer = ComplementaryAccent.copy(alpha = 0.3f),
    onTertiaryContainer = SecondaryPalette,
    
    error = Error80,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Error90,
    
    background = Color(0xFF121212),
    onBackground = SecondaryPalette,
    surface = Color(0xFF1A1C1E),
    onSurface = SecondaryPalette,
    surfaceVariant = Color(0xFF3F4948),
    onSurfaceVariant = ComplementaryAccent,
    
    outline = ComplementaryAccent,
    outlineVariant = ComplementaryAccent.copy(alpha = 0.5f)
)

/**
 * Light Color Scheme
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBase,
    onPrimary = Color.White,
    primaryContainer = PrimaryBase.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryBase,
    
    secondary = PrimaryAccent,
    onSecondary = Color.White,
    secondaryContainer = PrimaryAccent.copy(alpha = 0.15f),
    onSecondaryContainer = PrimaryBase,
    
    tertiary = ComplementaryAccent,
    onTertiary = Color.White,
    tertiaryContainer = ComplementaryAccent.copy(alpha = 0.2f),
    onTertiaryContainer = PrimaryBase,
    
    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Color(0xFF410002),
    
    background = SecondaryPalette,
    onBackground = PrimaryBase,
    surface = SecondaryPalette,
    onSurface = PrimaryBase,
    surfaceVariant = SecondaryPalette,
    onSurfaceVariant = PrimaryBase,
    
    outline = ComplementaryAccent,
    outlineVariant = ComplementaryAccent.copy(alpha = 0.5f)
)

@Composable
fun EPOSYANDUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default to ensure custom branding colors apply instead of Android 12+ wallpaper colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}