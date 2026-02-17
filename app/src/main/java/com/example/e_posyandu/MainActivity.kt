package com.example.e_posyandu

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.e_posyandu.ui.screen.MainScreen
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.theme.ThemeManager
import com.example.e_posyandu.ui.theme.LocalThemeManager
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        // Pastikan izin BLE runtime diberikan (Android 12+)
        ensureBlePermissions()
        
        setContent {
            val themeManager = remember { ThemeManager() }
            CompositionLocalProvider(LocalThemeManager provides themeManager) {
                EPOSYANDUTheme(darkTheme = themeManager.isDarkMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }

    private fun ensureBlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scanGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            val connectGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            if (!scanGranted || !connectGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    1001
                )
            }
        }
    }

}