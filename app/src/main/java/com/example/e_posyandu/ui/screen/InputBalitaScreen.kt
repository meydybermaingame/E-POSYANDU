package com.example.e_posyandu.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.data.repository.SensorData
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import com.example.e_posyandu.ui.utils.rememberResponsiveValues
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBalitaScreen(
    onNavigateBack: () -> Unit,
    viewModel: BalitaViewModel = viewModel()
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var namaBalita by remember { mutableStateOf("") }
    var namaAyah by remember { mutableStateOf("") }
    var namaIbu by remember { mutableStateOf("") }
    var usia by remember { mutableStateOf("") }
    var lingkarKepala by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var beratBadan by remember { mutableStateOf("") }
    var tinggiBadan by remember { mutableStateOf("") }
    var tanggalDaftar by remember { mutableStateOf("") }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Initialize date
    LaunchedEffect(Unit) {
        tanggalDaftar = dateFormat.format(Date())
    }
    
    // Reset form on success
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            namaBalita = ""
            namaAyah = ""
            namaIbu = ""
            usia = ""
            lingkarKepala = ""
            keterangan = ""
            beratBadan = ""
            tinggiBadan = ""
            tanggalDaftar = dateFormat.format(Date())
            viewModel.clearMessages()
        }
    }

    InputBalitaScreenContent(
        namaBalita = namaBalita,
        namaAyah = namaAyah,
        namaIbu = namaIbu,
        usia = usia,
        lingkarKepala = lingkarKepala,
        keterangan = keterangan,
        beratBadan = beratBadan,
        tinggiBadan = tinggiBadan,
        tanggalDaftar = tanggalDaftar,
        sensorData = sensorData,
        isLoading = isLoading,
        errorMessage = errorMessage,
        successMessage = successMessage,
        onNamaBalitaChange = { namaBalita = it },
        onNamaAyahChange = { namaAyah = it },
        onNamaIbuChange = { namaIbu = it },
        onUsiaChange = { usia = it },
        onLingkarKepalaChange = { lingkarKepala = it },
        onKeteranganChange = { keterangan = it },
        onBeratBadanChange = { beratBadan = it },
        onTinggiBadanChange = { tinggiBadan = it },
        onTanggalDaftarChange = { tanggalDaftar = it },
        onNavigateBack = onNavigateBack,
        onSubmit = {
            if (namaBalita.isNotEmpty() && usia.isNotEmpty() && 
                beratBadan.isNotEmpty() && tinggiBadan.isNotEmpty()) {
                val balita = Balita(
                    nama = namaBalita,
                    namaAyah = namaAyah,
                    namaIbu = namaIbu,
                    usia = usia.toIntOrNull() ?: 0,
                    lingkarKepala = lingkarKepala.toFloatOrNull() ?: 0f,
                    keterangan = keterangan,
                    berat = beratBadan.toFloatOrNull() ?: 0f,
                    tinggi = tinggiBadan.toFloatOrNull() ?: 0f,
                    tanggalDaftar = tanggalDaftar
                )
                viewModel.addBalita(balita)
            }
        },
        onTakeBerat = { sensorData?.berat?.let { beratBadan = String.format("%.1f", it) } },
        onTakeTinggi = { sensorData?.tinggi?.let { tinggiBadan = String.format("%.1f", it) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputBalitaScreenContent(
    namaBalita: String,
    namaAyah: String,
    namaIbu: String,
    usia: String,
    lingkarKepala: String,
    keterangan: String,
    beratBadan: String,
    tinggiBadan: String,
    tanggalDaftar: String,
    sensorData: SensorData?,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onNamaBalitaChange: (String) -> Unit,
    onNamaAyahChange: (String) -> Unit,
    onNamaIbuChange: (String) -> Unit,
    onUsiaChange: (String) -> Unit,
    onLingkarKepalaChange: (String) -> Unit,
    onKeteranganChange: (String) -> Unit,
    onBeratBadanChange: (String) -> Unit,
    onTinggiBadanChange: (String) -> Unit,
    onTanggalDaftarChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSubmit: () -> Unit,
    onTakeBerat: () -> Unit,
    onTakeTinggi: () -> Unit
) {
    val isConnected = sensorData?.connectionStatus == "connected"
    val responsive = rememberResponsiveValues()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Input Data Balita", fontWeight = FontWeight.Bold, fontSize = responsive.topBarTitleSize.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(responsive.contentPadding),
            verticalArrangement = Arrangement.spacedBy(responsive.cardSpacing)
        ) {
            // Title Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF667eea)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Form Pendaftaran Balita",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Masukkan data balita dengan lengkap",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // Sensor Status Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isConnected) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isConnected) "Sensor IoT Terhubung" else "Sensor IoT Tidak Terhubung",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                    
                    if (isConnected && sensorData != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Scale, null, tint = MaterialTheme.colorScheme.primary)
                                Text("Berat", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("${sensorData.berat} kg", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Straighten, null, tint = Color(0xFF667eea))
                                Text("Tinggi", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("${sensorData.tinggi} cm", fontWeight = FontWeight.Bold, color = Color(0xFF667eea))
                            }
                        }
                    }
                }
            }

            // Form Fields Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Data Pribadi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea)
                    )
                    
                    OutlinedTextField(
                        value = namaBalita,
                        onValueChange = onNamaBalitaChange,
                        label = { Text("Nama Balita *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = namaAyah,
                        onValueChange = onNamaAyahChange,
                        label = { Text("Nama Ayah") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = namaIbu,
                        onValueChange = onNamaIbuChange,
                        label = { Text("Nama Ibu") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = usia,
                        onValueChange = onUsiaChange,
                        label = { Text("Usia (tahun) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = lingkarKepala,
                        onValueChange = onLingkarKepalaChange,
                        label = { Text("Lingkar Kepala (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Circle, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = onKeteranganChange,
                        label = { Text("Keterangan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        leadingIcon = { Icon(Icons.Default.Note, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = tanggalDaftar,
                        onValueChange = onTanggalDaftarChange,
                        label = { Text("Tanggal Input") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                    )
                }
            }

            // Growth Data Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Data Pertumbuhan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = beratBadan,
                            onValueChange = onBeratBadanChange,
                            label = { Text("Berat (kg) *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) }
                        )
                        
                        OutlinedTextField(
                            value = tinggiBadan,
                            onValueChange = onTinggiBadanChange,
                            label = { Text("Tinggi (cm) *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onTakeBerat,
                            modifier = Modifier.weight(1f),
                            enabled = isConnected,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConnected) Color(0xFF4CAF50) else Color.Gray
                            )
                        ) {
                            Icon(Icons.Default.Scale, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ambil Berat")
                        }
                        
                        Button(
                            onClick = onTakeTinggi,
                            modifier = Modifier.weight(1f),
                            enabled = isConnected,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConnected) Color(0xFF2196F3) else Color.Gray
                            )
                        ) {
                            Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ambil Tinggi")
                        }
                    }
                    
                    if (!isConnected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "⚠️ Sensor tidak terhubung. Silakan hubungkan ESP32 terlebih dahulu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }

            // Error message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = Color(0xFFF44336), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(error, color = Color(0xFFF44336), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Success message
            AnimatedVisibility(
                visible = successMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                successMessage?.let { success ->
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(success, color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Submit Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(0.8f).height(responsive.buttonHeight).shadow(8.dp),
                    enabled = namaBalita.isNotEmpty() && usia.isNotEmpty() && 
                             beratBadan.isNotEmpty() && tinggiBadan.isNotEmpty() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (namaBalita.isNotEmpty() && usia.isNotEmpty() && 
                                           beratBadan.isNotEmpty() && tinggiBadan.isNotEmpty() && !isLoading)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Daftar Balita", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== PREVIEW SECTION ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InputBalitaScreenPreview() {
    EPOSYANDUTheme {
        InputBalitaScreenContent(
            namaBalita = "",
            namaAyah = "",
            namaIbu = "",
            usia = "",
            lingkarKepala = "",
            keterangan = "",
            beratBadan = "",
            tinggiBadan = "",
            tanggalDaftar = "28/12/2024",
            sensorData = null,
            isLoading = false,
            errorMessage = null,
            successMessage = null,
            onNamaBalitaChange = {},
            onNamaAyahChange = {},
            onNamaIbuChange = {},
            onUsiaChange = {},
            onLingkarKepalaChange = {},
            onKeteranganChange = {},
            onBeratBadanChange = {},
            onTinggiBadanChange = {},
            onTanggalDaftarChange = {},
            onNavigateBack = {},
            onSubmit = {},
            onTakeBerat = {},
            onTakeTinggi = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InputBalitaScreenFilledPreview() {
    EPOSYANDUTheme {
        InputBalitaScreenContent(
            namaBalita = "Ahmad Fauzi",
            namaAyah = "Budi Santoso",
            namaIbu = "Siti Aminah",
            usia = "2",
            lingkarKepala = "45.0",
            keterangan = "",
            beratBadan = "12.5",
            tinggiBadan = "85.0",
            tanggalDaftar = "28/12/2024",
            sensorData = SensorData(berat = 12.5f, tinggi = 85.0f, connectionStatus = "connected"),
            isLoading = false,
            errorMessage = null,
            successMessage = null,
            onNamaBalitaChange = {},
            onNamaAyahChange = {},
            onNamaIbuChange = {},
            onUsiaChange = {},
            onLingkarKepalaChange = {},
            onKeteranganChange = {},
            onBeratBadanChange = {},
            onTinggiBadanChange = {},
            onTanggalDaftarChange = {},
            onNavigateBack = {},
            onSubmit = {},
            onTakeBerat = {},
            onTakeTinggi = {}
        )
    }
}