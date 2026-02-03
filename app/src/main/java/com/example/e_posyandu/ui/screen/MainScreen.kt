package com.example.e_posyandu.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        )
        
        // Floating Bottom App Bar - positioned absolutely at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 12.dp,
                color = Color.White
            ) {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Home
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(route = Destination.HOME.route) {
                                            popUpTo(startDestination.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        selectedDestination = Destination.HOME.ordinal
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    Destination.HOME.icon,
                                    contentDescription = Destination.HOME.contentDescription,
                                    tint = if (selectedDestination == Destination.HOME.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Home",
                                    fontSize = 10.sp,
                                    color = if (selectedDestination == Destination.HOME.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                            
                            // Data Balita
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(route = Destination.DATA_BALITA.route) {
                                            popUpTo(startDestination.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        selectedDestination = Destination.DATA_BALITA.ordinal
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    Destination.DATA_BALITA.icon,
                                    contentDescription = Destination.DATA_BALITA.contentDescription,
                                    tint = if (selectedDestination == Destination.DATA_BALITA.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Data",
                                    fontSize = 10.sp,
                                    color = if (selectedDestination == Destination.DATA_BALITA.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                            
                            // Input Balita
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(route = Destination.INPUT.route) {
                                            popUpTo(startDestination.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        selectedDestination = Destination.INPUT.ordinal
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    Destination.INPUT.icon,
                                    contentDescription = Destination.INPUT.contentDescription,
                                    tint = if (selectedDestination == Destination.INPUT.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Input",
                                    fontSize = 10.sp,
                                    color = if (selectedDestination == Destination.INPUT.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                            
                            // Growth/Pertumbuhan
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(route = Destination.GROWTH.route) {
                                            popUpTo(startDestination.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        selectedDestination = Destination.GROWTH.ordinal
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    Destination.GROWTH.icon,
                                    contentDescription = Destination.GROWTH.contentDescription,
                                    tint = if (selectedDestination == Destination.GROWTH.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tumbuh",
                                    fontSize = 10.sp,
                                    color = if (selectedDestination == Destination.GROWTH.ordinal) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                        }
                    },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(route = Destination.EXPORT.route) {
                                popUpTo(startDestination.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedDestination = Destination.EXPORT.ordinal
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            Destination.EXPORT.icon,
                            contentDescription = Destination.EXPORT.contentDescription
                        )
                    }
                }
            )
            }
        }
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Content placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Main Content Placeholder")
        }
        
        // Floating Bottom App Bar - positioned absolutely at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 12.dp,
                color = Color.White
            ) {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Destination.entries.take(4).forEachIndexed { index, destination ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(
                                        destination.icon,
                                        contentDescription = destination.contentDescription,
                                        tint = if (selectedIndex == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (destination) {
                                            Destination.HOME -> "Home"
                                            Destination.DATA_BALITA -> "Data"
                                            Destination.INPUT -> "Input"
                                            Destination.GROWTH -> "Tumbuh"
                                            else -> ""
                                        },
                                        fontSize = 10.sp,
                                        color = if (selectedIndex == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* preview only */ },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(
                                Destination.EXPORT.icon,
                                contentDescription = Destination.EXPORT.contentDescription
                            )
                        }
                    }
                )
            }
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