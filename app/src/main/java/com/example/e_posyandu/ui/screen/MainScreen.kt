package com.example.e_posyandu.ui.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.utils.rememberResponsiveValues
import com.example.e_posyandu.ui.theme.LocalThemeManager

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

/**
 * Reusable animatable nav item: icon + label that lifts upward when selected.
 */
@Composable
private fun AnimatedNavItem(
    destination: Destination,
    isSelected: Boolean,
    iconSize: androidx.compose.ui.unit.Dp,
    labelFontSize: Int,
    onClick: () -> Unit
) {
    // Animate vertical offset: -16dp when selected, 0dp when not
    val offsetY by animateFloatAsState(
        targetValue = if (isSelected) -16f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navItemOffset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.4f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navItemAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .graphicsLayer { translationY = offsetY }
    ) {
        Icon(
            destination.icon,
            contentDescription = destination.contentDescription,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = destination.label,
            fontSize = labelFontSize.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
        )
    }
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
    val responsive = rememberResponsiveValues()
    val themeManager = LocalThemeManager.current
    
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
                color = if (themeManager.isDarkMode) MaterialTheme.colorScheme.surfaceContainer else Color.White
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
                            fun navigate(dest: Destination) {
                                navController.navigate(dest.route) {
                                    popUpTo(startDestination.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                selectedDestination = dest.ordinal
                            }

                            AnimatedNavItem(
                                destination = Destination.DATA_BALITA,
                                isSelected = selectedDestination == Destination.DATA_BALITA.ordinal,
                                iconSize = responsive.navIconSize,
                                labelFontSize = responsive.navFontSize,
                                onClick = { navigate(Destination.DATA_BALITA) }
                            )

                            AnimatedNavItem(
                                destination = Destination.INPUT,
                                isSelected = selectedDestination == Destination.INPUT.ordinal,
                                iconSize = responsive.navIconSize,
                                labelFontSize = responsive.navFontSize,
                                onClick = { navigate(Destination.INPUT) }
                            )

                            AnimatedNavItem(
                                destination = Destination.HOME,
                                isSelected = selectedDestination == Destination.HOME.ordinal,
                                iconSize = responsive.navIconSize,
                                labelFontSize = responsive.navFontSize,
                                onClick = { navigate(Destination.HOME) }
                            )

                            AnimatedNavItem(
                                destination = Destination.GROWTH,
                                isSelected = selectedDestination == Destination.GROWTH.ordinal,
                                iconSize = responsive.navIconSize,
                                labelFontSize = responsive.navFontSize,
                                onClick = { navigate(Destination.GROWTH) }
                            )
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