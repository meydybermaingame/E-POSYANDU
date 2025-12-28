package com.example.e_posyandu.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }
    
    if (showSplash) {
        SplashScreen(
            onSplashComplete = {
                showSplash = false
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF006064),
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = navController.currentDestination?.route == "home",
                        onClick = { navController.navigate("home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = Color(0xFF4DB6AC)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Data Balita") },
                        label = { Text("Data Balita") },
                        selected = navController.currentDestination?.route == "data_balita",
                        onClick = { navController.navigate("data_balita") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = Color(0xFF4DB6AC)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Input Balita") },
                        label = { Text("Input") },
                        selected = navController.currentDestination?.route == "input_balita",
                        onClick = { navController.navigate("input_balita") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = Color(0xFF4DB6AC)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Pertumbuhan") },
                        label = { Text("Pertumbuhan") },
                        selected = navController.currentDestination?.route == "pertumbuhan",
                        onClick = { navController.navigate("pertumbuhan") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = Color(0xFF4DB6AC)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Download, contentDescription = "Ekspor") },
                        label = { Text("Ekspor") },
                        selected = navController.currentDestination?.route == "export_csv",
                        onClick = { navController.navigate("export_csv") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = Color(0xFF4DB6AC)
                        )
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    HomeScreen(
                        onMenuClick = { menu ->
                            when (menu.label) {
                                "Input Data" -> navController.navigate("input_balita")
                                "Data Balita" -> navController.navigate("data_balita")
                                "Pertumbuhan Balita" -> navController.navigate("pertumbuhan")
                                "Edit Data" -> navController.navigate("data_balita") // Edit via list
                                "Cetak" -> navController.navigate("export_csv")
                            }
                        }
                    )
                }
                composable("data_balita") {
                    DataBalitaScreen(
                        onNavigateToInput = { navController.navigate("input_balita") },
                        onNavigateToDetail = { namaBalita ->
                            navController.navigate("detail_balita/$namaBalita")
                        },
                        onNavigateToEdit = { namaBalita ->
                            navController.navigate("edit_balita/$namaBalita")
                        }
                    )
                }
                composable("input_balita") {
                    InputBalitaScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("pertumbuhan") {
                    PertumbuhanScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("export_csv") {
                    ExportCsvScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("detail_balita/{namaBalita}") { backStackEntry ->
                    val namaBalita = backStackEntry.arguments?.getString("namaBalita") ?: ""
                    DetailBalitaScreen(
                        namaBalita = namaBalita,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEdit = { navController.navigate("edit_balita/$namaBalita") }
                    )
                }
                composable("edit_balita/{namaBalita}") { backStackEntry ->
                    val namaBalita = backStackEntry.arguments?.getString("namaBalita") ?: ""
                    EditBalitaScreen(
                        namaBalita = namaBalita,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

// ==================== PREVIEW SECTION ====================
/**
 * Preview untuk MainScreen - menampilkan layout bottom navigation dengan content placeholder.
 * Karena MainScreen mengandung navigasi kompleks dan ViewModels, 
 * preview hanya menampilkan struktur UI dasar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenPreviewContent() {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF006064),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color(0xFF4DB6AC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Data Balita") },
                    label = { Text("Data") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color(0xFF4DB6AC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Input") },
                    label = { Text("Input") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color(0xFF4DB6AC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Pertumbuhan") },
                    label = { Text("Growth") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color(0xFF4DB6AC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Download, contentDescription = "Export") },
                    label = { Text("Export") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color(0xFF4DB6AC)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Text("Main Content Placeholder")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    EPOSYANDUTheme {
        MainScreenPreviewContent()
    }
}