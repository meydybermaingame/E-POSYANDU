package com.example.e_posyandu.ui.screen
import com.example.e_posyandu.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.e_posyandu.data.repository.BleRepository
import androidx.compose.material3.HorizontalDivider
import com.example.e_posyandu.utils.DummyDataGenerator
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.e_posyandu.BuildConfig
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme

// Enhanced menu data class with icons and colors
data class HomeMenu(
    val label: String, 
    val icon: Int,
    val gradient: Brush,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: (HomeMenu) -> Unit = {},
    viewModel: BalitaViewModel = viewModel()
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    
    // State untuk menampilkan dialog konfirmasi
    var showDummyDataDialog by remember { mutableStateOf(false) }
    var isGeneratingData by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Determine connection state
    val isConnected = connectionStatus == "connected"
    val isError = connectionStatus == "error"
    val isConnectingState = connectionStatus == "connecting" || isConnecting
    
    // Animated connection indicator
    val connectionAnimation by animateFloatAsState(
        targetValue = if (isConnected) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    
    // Pulse animation for connection indicator
    val pulseAnimation by rememberInfiniteTransition().animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val menuList = listOf(
        HomeMenu(
            "Input Data", 
            R.drawable.ic_input_data,
            Brush.linearGradient(listOf(Color(0xFF00695C), Color(0xFF4DB6AC))),
            "Tambah data balita baru"
        ),
        HomeMenu(
            "Data Balita", 
            R.drawable.ic_data_balita,
            Brush.linearGradient(listOf(Color(0xFF29B6F6), Color(0xFF80CBC4))),
            "Lihat semua data balita"
        ),
        HomeMenu(
            "Pertumbuhan Balita", 
            R.drawable.ic_pertumbuhan,
            Brush.linearGradient(listOf(Color(0xFF388E3C), Color(0xFFA5D6A7))),
            "Monitor pertumbuhan balita"
        ),
        HomeMenu(
            "Edit Data", 
            R.drawable.ic_edit_data,
            Brush.linearGradient(listOf(Color(0xFF9575CD), Color(0xFFB2DFDB))),
            "Edit data balita"
        ),
        HomeMenu(
            "Cetak", 
            R.drawable.ic_cetak,
            Brush.linearGradient(listOf(Color(0xFFFF7043), Color(0xFFFF8A65))),
            "Export data ke CSV"
        ),
        HomeMenu(
            "Data Demo", 
            R.drawable.ic_input_data, // Menggunakan icon yang sama dengan Input Data
            Brush.linearGradient(listOf(Color(0xFF7B1FA2), Color(0xFFBA68C8))),
            "Buat data balita demo"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF006064),
                        Color(0xFF00695C)
                    )
                )
            )
    ) {
        // Enhanced TopAppBar with connection indicator
        TopAppBar(
            title = {
                Text(
                    "E-POSYANDU",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                // ESP32 Connection Indicator with enhanced status
                Card(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(48.dp)
                        .shadow(
                            elevation = if (isConnected) 8.dp else 2.dp,
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isConnected -> Color(0xFF4CAF50)
                            isError -> Color(0xFFF44336)
                            isConnectingState -> Color(0xFFFF9800)
                            else -> Color(0xFF9E9E9E)
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isConnected -> Icons.Default.BluetoothConnected
                                isError -> Icons.Default.BluetoothDisabled
                                isConnectingState -> Icons.Default.BluetoothSearching
                                else -> Icons.Default.Bluetooth
                            },
                            contentDescription = when {
                                isConnected -> "Bluetooth Connected"
                                isError -> "Bluetooth Connection Error"
                                isConnectingState -> "Bluetooth Connecting"
                                else -> "Bluetooth Disconnected"
                            },
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        // Animated pulse effect when connected or connecting
                        if (isConnected || isConnectingState) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp * pulseAnimation)
                                    .background(
                                        when {
                                            isConnected -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                                            else -> Color(0xFFFF9800).copy(alpha = 0.3f)
                                        },
                                        CircleShape
                                    )
                            )
                        }
                        
                        // Rotating animation for connecting state
                        if (isConnectingState) {
                            val rotation by rememberInfiniteTransition().animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(2000, easing = LinearEasing)
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Connecting",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(32.dp)
                                    .graphicsLayer(rotationZ = rotation)
                            )
                        }
                    }
                }
                
                // Profile/Admin picture
                Card(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(48.dp)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Admin Profile",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF006064)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF006064)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        
        // Panel BLE untuk koneksi langsung ke ESP32 via Bluetooth
        run {
            val context = LocalContext.current
            val bleRepo = remember { BleRepository(context) }
            val bleConnected by bleRepo.connectionState.collectAsState(initial = false)
            val bleReading by bleRepo.sensorData.collectAsState(initial = null)

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (bleConnected) Color(0xFFE8F5E9) else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon Bluetooth besar di tengah
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = if (bleConnected) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                                        else Color(0xFF9E9E9E).copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (bleConnected) Icons.Default.BluetoothConnected 
                                          else Icons.Default.Bluetooth,
                            contentDescription = null,
                            tint = if (bleConnected) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Status Text
                    Text(
                        text = if (bleConnected) "Alat Terhubung" else "Alat Belum Tersambung",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (bleConnected) Color(0xFF4CAF50) else Color(0xFF757575),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = if (bleConnected) "ESP32 siap digunakan via Bluetooth" 
                               else "Klik tombol di bawah untuk menghubungkan",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Tombol utama (Scan/Disconnect)
                    Button(
                        onClick = { 
                            if (bleConnected) bleRepo.disconnect() 
                            else bleRepo.startScan() 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (bleConnected) Color(0xFFF44336) else Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            imageVector = if (bleConnected) Icons.Default.PowerSettingsNew 
                                          else Icons.Default.BluetoothSearching,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (bleConnected) "Putuskan Koneksi" else "Hubungkan Alat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Tombol Kalibrasi hanya muncul jika terhubung
                    if (bleConnected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { bleRepo.sendCommand("calibrate") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kalibrasi Sensor", fontWeight = FontWeight.Medium)
                        }
                    }

                    if (bleReading != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Berat Badan", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.2f kg", bleReading!!.beratKg),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Tinggi Badan", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.1f cm", bleReading!!.tinggiCm),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show error/success messages
        AnimatedVisibility(
            visible = errorMessage != null || successMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (errorMessage != null) Color(0xFFFFEBEE) else Color(0xFFE8F5E8)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (errorMessage != null) Icons.Default.Error else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (errorMessage != null) Color(0xFFF44336) else Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage ?: successMessage ?: "",
                        fontSize = 14.sp,
                        color = if (errorMessage != null) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Enhanced title
        Text(
            "Fitur Layanan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
        
        // Enhanced Grid Menu with animations
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuList) { menu ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    Card(
                        onClick = { 
                            if (menu.label == "Data Demo") {
                                showDummyDataDialog = true
                            } else {
                                onMenuClick(menu)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(menu.gradient)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = menu.icon),
                                    contentDescription = menu.label,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    menu.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    menu.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
    
    // Dialog konfirmasi untuk generate data dummy
    if (showDummyDataDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isGeneratingData) {
                    showDummyDataDialog = false
                }
            },
            title = {
                Text("Buat Data Demo Balita")
            },
            text = {
                Column {
                    Text("Apakah Anda ingin membuat data demo balita?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Data ini akan menambahkan 5 balita dengan riwayat pertumbuhan untuk keperluan testing aplikasi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    
                    if (isGeneratingData) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sedang membuat data demo...")
                        }
                    }
                }
            },
            confirmButton = {
                 TextButton(
                     onClick = {
                         isGeneratingData = true
                         
                         // Test Firebase connection first
                         testFirebaseConnection()
                         testBalitaPath()
                         debugFirebaseConnection()
                         
                         val generator = DummyDataGenerator()
                         generator.generateDummyData()
                     },
                     enabled = !isGeneratingData
                 ) {
                     Text("Ya, Buat Data")
                 }
             },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDummyDataDialog = false 
                    },
                    enabled = !isGeneratingData
                ) {
                    Text("Batal")
                }
            }
        )
    }
    
    // Handle data generation completion
     LaunchedEffect(isGeneratingData) {
         if (isGeneratingData) {
             kotlinx.coroutines.delay(3000) // Simulate processing time
             isGeneratingData = false
             showDummyDataDialog = false
             showSuccessMessage = true
             
             // Auto hide success message
             kotlinx.coroutines.delay(3000)
             showSuccessMessage = false
         }
     }
     
     // Show success message
      if (showSuccessMessage) {
          LaunchedEffect(Unit) {
              // Data akan otomatis ter-refresh melalui Flow dari repository
              println("✅ Data dummy berhasil ditambahkan!")
          }
      }
}

// Debug function untuk memeriksa koneksi Firebase
fun debugFirebaseConnection() {
    // Try without explicit URL first
    val database = FirebaseDatabase.getInstance()
    val testRef = database.getReference("test")
    
    Log.d("HomeScreen", "🔥 Testing Firebase connection (using google-services.json)...")
    
    // Test write operation
    testRef.setValue("Hello Firebase Test")
        .addOnSuccessListener {
            Log.d("HomeScreen", "✅ Firebase write successful")
            
            // Test read operation
            testRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(String::class.java)
                    Log.d("HomeScreen", "✅ Firebase read successful: $value")
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeScreen", "❌ Firebase read failed: ${error.message}")
                    Log.d("HomeScreen", "🔄 Error details: ${error.details}")
                }
            })
        }
        .addOnFailureListener { exception ->
            Log.e("HomeScreen", "❌ Firebase write failed: ${exception.message}")
        }
    
    // Check balita data count
    val balitaRef = database.getReference("balita")
    balitaRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val count = snapshot.childrenCount
            Log.d("HomeScreen", "📊 Balita count in database: $count")
            
            snapshot.children.forEach { child ->
                val nama = child.child("nama").getValue(String::class.java)
                Log.d("HomeScreen", "👶 Balita: ${child.key} - $nama")
            }
        }
        
        override fun onCancelled(error: DatabaseError) {
            Log.e("HomeScreen", "❌ Balita read failed: ${error.message}")
        }
    })
}

// Test Firebase connection with different approach
fun testFirebaseConnection() {
    Log.d("FirebaseTest", "🚀 Starting Firebase connection test...")
    
    try {
        // Try without explicit URL first - let it use google-services.json
        val database = FirebaseDatabase.getInstance()
        val testRef = database.getReference("test")
        
        Log.d("FirebaseTest", "📡 Database instance created (using google-services.json)")
        
        // Test write
        testRef.setValue("Hello Firebase")
            .addOnSuccessListener {
                Log.d("FirebaseTest", "✅ Write successful")
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseTest", "❌ Write failed: ${exception.message}")
                
                // Try with explicit URL as fallback
                Log.d("FirebaseTest", "🔄 Trying with explicit URL...")
                val databaseWithUrl = FirebaseDatabase.getInstance("https://e-posyandu-app-default-rtdb.asia-southeast1.firebasedatabase.app")
                val testRefWithUrl = databaseWithUrl.getReference("test")
                
                testRefWithUrl.setValue("Hello Firebase with URL")
                    .addOnSuccessListener {
                        Log.d("FirebaseTest", "✅ Write with URL successful")
                    }
                    .addOnFailureListener { urlException ->
                        Log.e("FirebaseTest", "❌ Write with URL failed: ${urlException.message}")
                    }
            }
    } catch (e: Exception) {
        Log.e("FirebaseTest", "❌ Firebase initialization failed: ${e.message}")
    }
}

// Test balita path specifically
fun testBalitaPath() {
    Log.d("FirebaseTest", "🔍 Testing balita path...")
    
    try {
        // Try without explicit URL first
        val database = FirebaseDatabase.getInstance()
        val balitaRef = database.getReference("balita")
        
        balitaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                Log.d("FirebaseTest", "✅ Balita count: $count")
                
                if (count > 0) {
                    snapshot.children.forEach { child ->
                        Log.d("FirebaseTest", "👶 Balita ID: ${child.key}")
                    }
                } else {
                    Log.d("FirebaseTest", "📭 No balita data found")
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseTest", "❌ Balita read failed: ${error.message}")
                Log.d("FirebaseTest", "🔄 Error details: ${error.details}")
            }
        })
    } catch (e: Exception) {
        Log.e("FirebaseTest", "❌ Balita path test failed: ${e.message}")
    }
}

// ==================== PREVIEW SECTION ====================

/**
 * Preview-friendly version of HomeScreen yang tidak membutuhkan ViewModel atau dependencies platform
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    menuList: List<HomeMenu>,
    isConnected: Boolean = false,
    isConnecting: Boolean = false,
    isError: Boolean = false,
    bleConnected: Boolean = false,
    bleWeight: Float? = null,
    bleHeight: Float? = null,
    errorMessage: String? = null,
    successMessage: String? = null,
    onMenuClick: (HomeMenu) -> Unit = {},
    onConnectClick: () -> Unit = {},
    onDisconnectClick: () -> Unit = {},
    onCalibrateClick: () -> Unit = {}
) {
    // Pulse animation for connection indicator
    val pulseAnimation by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF006064),
                        Color(0xFF00695C)
                    )
                )
            )
    ) {
        // Enhanced TopAppBar with connection indicator
        TopAppBar(
            title = {
                Text(
                    "E-POSYANDU",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                // ESP32 Connection Indicator with enhanced status
                Card(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(48.dp)
                        .shadow(
                            elevation = if (isConnected) 8.dp else 2.dp,
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isConnected -> Color(0xFF4CAF50)
                            isError -> Color(0xFFF44336)
                            isConnecting -> Color(0xFFFF9800)
                            else -> Color(0xFF9E9E9E)
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isConnected -> Icons.Default.BluetoothConnected
                                isError -> Icons.Default.BluetoothDisabled
                                isConnecting -> Icons.Default.BluetoothSearching
                                else -> Icons.Default.Bluetooth
                            },
                            contentDescription = when {
                                isConnected -> "Bluetooth Connected"
                                isError -> "Bluetooth Connection Error"
                                isConnecting -> "Bluetooth Connecting"
                                else -> "Bluetooth Disconnected"
                            },
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        // Animated pulse effect when connected or connecting
                        if (isConnected || isConnecting) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp * pulseAnimation)
                                    .background(
                                        when {
                                            isConnected -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                                            else -> Color(0xFFFF9800).copy(alpha = 0.3f)
                                        },
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
                
                // Profile/Admin picture
                Card(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(48.dp)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Admin Profile",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF006064)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF006064)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Panel BLE untuk koneksi langsung ke ESP32 via Bluetooth
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (bleConnected) Color(0xFFE8F5E9) else Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Bluetooth besar di tengah
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (bleConnected) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                                    else Color(0xFF9E9E9E).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (bleConnected) Icons.Default.BluetoothConnected 
                                      else Icons.Default.Bluetooth,
                        contentDescription = null,
                        tint = if (bleConnected) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Status Text
                Text(
                    text = if (bleConnected) "Alat Terhubung" else "Alat Belum Tersambung",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (bleConnected) Color(0xFF4CAF50) else Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (bleConnected) "ESP32 siap digunakan via Bluetooth" 
                           else "Klik tombol di bawah untuk menghubungkan",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Tombol utama (Scan/Disconnect)
                Button(
                    onClick = { 
                        if (bleConnected) onDisconnectClick() 
                        else onConnectClick() 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bleConnected) Color(0xFFF44336) else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = if (bleConnected) Icons.Default.PowerSettingsNew 
                                      else Icons.Default.BluetoothSearching,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (bleConnected) "Putuskan Koneksi" else "Hubungkan Alat",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Tombol Kalibrasi hanya muncul jika terhubung
                if (bleConnected) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onCalibrateClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kalibrasi Sensor", fontWeight = FontWeight.Medium)
                    }
                }

                if (bleWeight != null && bleHeight != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Berat Badan", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = String.format("%.2f kg", bleWeight),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Tinggi Badan", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = String.format("%.1f cm", bleHeight),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }
        }

        // Show error/success messages
        AnimatedVisibility(
            visible = errorMessage != null || successMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (errorMessage != null) Color(0xFFFFEBEE) else Color(0xFFE8F5E8)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (errorMessage != null) Icons.Default.Error else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (errorMessage != null) Color(0xFFF44336) else Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage ?: successMessage ?: "",
                        fontSize = 14.sp,
                        color = if (errorMessage != null) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Enhanced title
        Text(
            "Fitur Layanan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
        
        // Enhanced Grid Menu with animations
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuList) { menu ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    Card(
                        onClick = { onMenuClick(menu) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(menu.gradient)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = menu.icon),
                                    contentDescription = menu.label,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    menu.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    menu.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val previewMenuList = listOf(
        HomeMenu(
            "Input Data", 
            R.drawable.ic_input_data,
            Brush.linearGradient(listOf(Color(0xFF00695C), Color(0xFF4DB6AC))),
            "Tambah data balita baru"
        ),
        HomeMenu(
            "Data Balita", 
            R.drawable.ic_data_balita,
            Brush.linearGradient(listOf(Color(0xFF29B6F6), Color(0xFF80CBC4))),
            "Lihat semua data balita"
        ),
        HomeMenu(
            "Pertumbuhan Balita", 
            R.drawable.ic_pertumbuhan,
            Brush.linearGradient(listOf(Color(0xFF388E3C), Color(0xFFA5D6A7))),
            "Monitor pertumbuhan balita"
        ),
        HomeMenu(
            "Edit Data", 
            R.drawable.ic_edit_data,
            Brush.linearGradient(listOf(Color(0xFF9575CD), Color(0xFFB2DFDB))),
            "Edit data balita"
        )
    )
    
    EPOSYANDUTheme {
        HomeScreenContent(
            menuList = previewMenuList,
            isConnected = false,
            bleConnected = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home Screen Connected")
@Composable
fun HomeScreenConnectedPreview() {
    val previewMenuList = listOf(
        HomeMenu(
            "Input Data", 
            R.drawable.ic_input_data,
            Brush.linearGradient(listOf(Color(0xFF00695C), Color(0xFF4DB6AC))),
            "Tambah data balita baru"
        ),
        HomeMenu(
            "Data Balita", 
            R.drawable.ic_data_balita,
            Brush.linearGradient(listOf(Color(0xFF29B6F6), Color(0xFF80CBC4))),
            "Lihat semua data balita"
        )
    )
    
    EPOSYANDUTheme {
        HomeScreenContent(
            menuList = previewMenuList,
            isConnected = true,
            bleConnected = true,
            bleWeight = 12.5f,
            bleHeight = 85.5f
        )
    }
}
