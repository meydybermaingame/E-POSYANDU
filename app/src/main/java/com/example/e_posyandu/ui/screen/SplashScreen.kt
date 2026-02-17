package com.example.e_posyandu.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.utils.rememberResponsiveValues
import kotlinx.coroutines.delay

@OptIn(ExperimentalTextApi::class, ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val responsive = rememberResponsiveValues()
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    
    // Multiple animation states
    val logoScale by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    val logoRotation by animateFloatAsState(
        targetValue = if (showLogo) 360f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoRotation"
    )
    
    val titleAlpha by animateFloatAsState(
        targetValue = if (showTitle) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 500),
        label = "titleAlpha"
    )
    
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (showSubtitle) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 800),
        label = "subtitleAlpha"
    )
    
    // Floating animation for background elements
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    
    LaunchedEffect(key1 = true) {
        delay(200)
        showLogo = true
        delay(600)
        showTitle = true
        delay(300)
        showSubtitle = true
        delay(2000)
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    ),
                    radius = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background floating elements
        repeat(6) { index ->
            val delay = index * 300f
            val offsetY by infiniteTransition.animateFloat(
                initialValue = -20f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + (index * 200),
                        easing = LinearEasing,
                        delayMillis = delay.toInt()
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "backgroundFloat$index"
            )
            
            Box(
                modifier = Modifier
                    .offset(
                        x = ((-200 + index * 80).dp),
                        y = ((-150 + index * 60 + offsetY).dp)
                    )
                    .size((30 + index * 10).dp)
                    .alpha(0.1f)
                    .background(
                        Color.White,
                        CircleShape
                    )
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(responsive.horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo container
            Card(
                modifier = Modifier
                    .size(if (responsive.isTablet) 180.dp else 140.dp)
                    .scale(logoScale)
                    .rotate(logoRotation * 0.3f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Gradient background for icon
                    Box(
                        modifier = Modifier
                            .size(if (responsive.isTablet) 130.dp else 100.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.primary
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "E-Posyandu Logo",
                            modifier = Modifier.size(if (responsive.isTablet) 65.dp else 50.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Animated title
            Text(
                text = "E-POSYANDU",
                modifier = Modifier.alpha(titleAlpha),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = responsive.titleFontSize.sp,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Animated subtitle with background
            Card(
                modifier = Modifier
                    .alpha(subtitleAlpha)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Sistem Informasi Posyandu Digital",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = responsive.bodyFontSize.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Loading indicator dengan label
            if (showSubtitle) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .alpha(0.7f),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Memuat...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

// ==================== PREVIEW SECTION ====================
@OptIn(ExperimentalTextApi::class, ExperimentalAnimationApi::class)
@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    ),
                    radius = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.size(140.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "E-Posyandu Logo",
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "E-POSYANDU",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Sistem Informasi Posyandu Digital",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp).alpha(0.7f),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    EPOSYANDUTheme {
        SplashScreenContent()
    }
}