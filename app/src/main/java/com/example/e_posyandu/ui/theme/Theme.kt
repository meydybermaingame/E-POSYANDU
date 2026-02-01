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
 * Dark Color Scheme - Material3 dengan complete color roles
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = Teal80,
    onPrimary = Teal10,
    primaryContainer = Teal30,
    onPrimaryContainer = Teal90,
    
    // Secondary colors
    secondary = Mint80,
    onSecondary = Mint10,
    secondaryContainer = Mint30,
    onSecondaryContainer = Mint90,
    
    // Tertiary colors
    tertiary = Ocean80,
    onTertiary = Ocean10,
    tertiaryContainer = Ocean30,
    onTertiaryContainer = Ocean90,
    
    // Error colors
    error = Error80,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Error90,
    
    // Surface colors
    background = Color(0xFF121212),
    onBackground = Neutral90,
    surface = Color(0xFF1A1C1E),
    onSurface = Neutral90,
    surfaceVariant = Color(0xFF3F4948),
    onSurfaceVariant = Color(0xFFBEC9C7),
    
    // Outline
    outline = Color(0xFF6F7978),
    outlineVariant = Color(0xFF3F4948),
    
    // Inverse colors
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral10,
    inversePrimary = Teal40
)

/**
 * Light Color Scheme - Material3 dengan complete color roles
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = Teal40,
    onPrimary = Color.White,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal10,
    
    // Secondary colors
    secondary = Mint40,
    onSecondary = Color.White,
    secondaryContainer = Mint90,
    onSecondaryContainer = Mint10,
    
    // Tertiary colors
    tertiary = Ocean40,
    onTertiary = Color.White,
    tertiaryContainer = Ocean90,
    onTertiaryContainer = Ocean10,
    
    // Error colors
    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Color(0xFF410002),
    
    // Surface colors
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = Neutral95,
    onSurfaceVariant = Color(0xFF3F4948),
    
    // Outline
    outline = Color(0xFF6F7978),
    outlineVariant = Color(0xFFBEC9C7),
    
    // Inverse colors
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = Teal80
)

@Composable
fun EPOSYANDUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        shapes = Shapes,  // ✨ Added Shapes system
        content = content
    )
}