package com.example.e_posyandu.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme

/**
 * Destination enum untuk bottom navigation
 * Setiap destination memiliki route, label, icon, dan content description
 */
enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME(
        route = "home",
        label = "Home",
        icon = Icons.Default.Home,
        contentDescription = "Home Screen"
    ),
    DATA_BALITA(
        route = "data_balita",
        label = "Data",
        icon = Icons.Default.List,
        contentDescription = "Data Balita Screen"
    ),
    INPUT(
        route = "input_balita",
        label = "Input",
        icon = Icons.Default.Add,
        contentDescription = "Input Balita Screen"
    ),
    GROWTH(
        route = "pertumbuhan",
        label = "Growth",
        icon = Icons.Default.TrendingUp,
        contentDescription = "Pertumbuhan Screen"
    ),
    EXPORT(
        route = "export_csv",
        label = "Export",
        icon = Icons.Default.Download,
        contentDescription = "Export Screen"
    )
}

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
        MainScreenContent(navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(navController: NavHostController) {
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route) {
                                // Pop up to start destination to avoid building up a large stack
                                popUpTo(startDestination.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

/**
 * Navigation host untuk mengatur semua routes
 */
@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Destination.HOME.route) {
            HomeScreen(
                onMenuClick = { menu ->
                    when (menu.label) {
                        "Input Data" -> navController.navigate(Destination.INPUT.route)
                        "Data Balita" -> navController.navigate(Destination.DATA_BALITA.route)
                        "Pertumbuhan Balita" -> navController.navigate(Destination.GROWTH.route)
                        "Edit Data" -> navController.navigate(Destination.DATA_BALITA.route)
                        "Cetak" -> navController.navigate(Destination.EXPORT.route)
                    }
                }
            )
        }
        
        composable(Destination.DATA_BALITA.route) {
            DataBalitaScreen(
                onNavigateToInput = { navController.navigate(Destination.INPUT.route) },
                onNavigateToDetail = { namaBalita ->
                    navController.navigate("detail_balita/$namaBalita")
                },
                onNavigateToEdit = { namaBalita ->
                    navController.navigate("edit_balita/$namaBalita")
                }
            )
        }
        
        composable(Destination.INPUT.route) {
            InputBalitaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Destination.GROWTH.route) {
            PertumbuhanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Destination.EXPORT.route) {
            ExportCsvScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Detail screens (not in bottom nav)
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

// ==================== PREVIEW SECTION ====================

/**
 * Preview untuk MainScreen - menampilkan layout bottom navigation dengan content placeholder.
 * Menggunakan Destination enum dan MaterialTheme colors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenPreviewContent() {
    val selectedIndex = 0
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                destination.icon, 
                                contentDescription = destination.contentDescription
                            ) 
                        },
                        label = { Text(destination.label) },
                        selected = selectedIndex == index,
                        onClick = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

@Preview(
    showBackground = true, 
    showSystemUi = true,
    name = "Dark Theme"
)
@Composable
fun MainScreenPreviewDark() {
    EPOSYANDUTheme(darkTheme = true) {
        MainScreenPreviewContent()
    }
}