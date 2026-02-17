package com.example.e_posyandu.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.data.repository.SensorData
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import com.example.e_posyandu.ui.utils.rememberResponsiveValues
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.preview.PreviewSampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBalitaScreen(
    namaBalita: String,
    onNavigateBack: () -> Unit,
    viewModel: BalitaViewModel = viewModel()
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    var balita by remember { mutableStateOf<Balita?>(null) }
    var namaAyah by remember { mutableStateOf("") }
    var namaIbu by remember { mutableStateOf("") }
    var usia by remember { mutableStateOf("") }
    var lingkarKepala by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var beratBadan by remember { mutableStateOf("") }
    var tinggiBadan by remember { mutableStateOf("") }

    LaunchedEffect(namaBalita) {
        viewModel.searchBalita(namaBalita).firstOrNull()?.let { foundBalita ->
            balita = foundBalita
            namaAyah = foundBalita.namaAyah
            namaIbu = foundBalita.namaIbu
            usia = foundBalita.usia.toString()
            lingkarKepala = foundBalita.lingkarKepala.toString()
            keterangan = foundBalita.keterangan
            beratBadan = foundBalita.berat.toString()
            tinggiBadan = foundBalita.tinggi.toString()
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    EditBalitaScreenContent(
        balita = balita,
        namaAyah = namaAyah,
        namaIbu = namaIbu,
        usia = usia,
        lingkarKepala = lingkarKepala,
        keterangan = keterangan,
        beratBadan = beratBadan,
        tinggiBadan = tinggiBadan,
        sensorData = sensorData,
        isLoading = isLoading,
        errorMessage = errorMessage,
        successMessage = successMessage,
        onNamaAyahChange = { namaAyah = it },
        onNamaIbuChange = { namaIbu = it },
        onUsiaChange = { usia = it },
        onLingkarKepalaChange = { lingkarKepala = it },
        onKeteranganChange = { keterangan = it },
        onBeratBadanChange = { beratBadan = it },
        onTinggiBadanChange = { tinggiBadan = it },
        onNavigateBack = onNavigateBack,
        onUpdate = {
            if (usia.isNotEmpty() && beratBadan.isNotEmpty() && tinggiBadan.isNotEmpty()) {
                val updatedBalita = balita?.copy(
                    namaAyah = namaAyah,
                    namaIbu = namaIbu,
                    usia = usia.toIntOrNull() ?: 0,
                    lingkarKepala = lingkarKepala.toFloatOrNull() ?: 0f,
                    keterangan = keterangan,
                    berat = beratBadan.toFloatOrNull() ?: 0f,
                    tinggi = tinggiBadan.toFloatOrNull() ?: 0f
                )
                updatedBalita?.let { viewModel.updateBalita(it) }
            }
        },
        onTakeBerat = { sensorData?.berat?.let { beratBadan = it.toString() } },
        onTakeTinggi = { sensorData?.tinggi?.let { tinggiBadan = it.toString() } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBalitaScreenContent(
    balita: Balita?,
    namaAyah: String,
    namaIbu: String,
    usia: String,
    lingkarKepala: String,
    keterangan: String,
    beratBadan: String,
    tinggiBadan: String,
    sensorData: SensorData?,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onNamaAyahChange: (String) -> Unit,
    onNamaIbuChange: (String) -> Unit,
    onUsiaChange: (String) -> Unit,
    onLingkarKepalaChange: (String) -> Unit,
    onKeteranganChange: (String) -> Unit,
    onBeratBadanChange: (String) -> Unit,
    onTinggiBadanChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onUpdate: () -> Unit,
    onTakeBerat: () -> Unit,
    onTakeTinggi: () -> Unit
) {
    val responsive = rememberResponsiveValues()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Data Balita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (balita == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(responsive.contentPadding),
                verticalArrangement = Arrangement.spacedBy(responsive.cardSpacing)
            ) {
                Text(
                    text = "Edit Data Balita",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = balita.nama,
                    onValueChange = { },
                    label = { Text("Nama Balita") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false
                )

                // Sensor data display
                if (sensorData?.connectionStatus == "connected") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Data Sensor IoT",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Berat: ${sensorData.berat} kg")
                                Text("Tinggi: ${sensorData.tinggi} cm")
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = namaAyah,
                    onValueChange = onNamaAyahChange,
                    label = { Text("Nama Ayah") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = namaIbu,
                    onValueChange = onNamaIbuChange,
                    label = { Text("Nama Ibu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = usia,
                    onValueChange = onUsiaChange,
                    label = { Text("Usia (tahun) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = lingkarKepala,
                    onValueChange = onLingkarKepalaChange,
                    label = { Text("Lingkar Kepala (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = onKeteranganChange,
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                // Weight and height section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Data Pertumbuhan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = beratBadan,
                                onValueChange = onBeratBadanChange,
                                label = { Text("Berat Badan (kg) *") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = tinggiBadan,
                                onValueChange = onTinggiBadanChange,
                                label = { Text("Tinggi Badan (cm) *") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onTakeBerat,
                                modifier = Modifier.weight(1f),
                                enabled = sensorData?.connectionStatus == "connected"
                            ) {
                                Icon(Icons.Default.Scale, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ambil Berat")
                            }
                            
                            Button(
                                onClick = onTakeTinggi,
                                modifier = Modifier.weight(1f),
                                enabled = sensorData?.connectionStatus == "connected"
                            ) {
                                Icon(Icons.Default.Straighten, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ambil Tinggi")
                            }
                        }
                    }
                }

                // Error message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Update button
                Button(
                    onClick = onUpdate,
                    modifier = Modifier.fillMaxWidth().height(responsive.buttonHeight),
                    enabled = usia.isNotEmpty() && beratBadan.isNotEmpty() && 
                             tinggiBadan.isNotEmpty() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Update Data")
                    }
                }
            }
        }
    }
}

// ==================== PREVIEW SECTION ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditBalitaScreenPreview() {
    EPOSYANDUTheme {
        EditBalitaScreenContent(
            balita = PreviewSampleData.singleBalita,
            namaAyah = "Budi Santoso",
            namaIbu = "Siti Aminah",
            usia = "2",
            lingkarKepala = "45.0",
            keterangan = "",
            beratBadan = "12.5",
            tinggiBadan = "85.0",
            sensorData = null,
            isLoading = false,
            errorMessage = null,
            successMessage = null,
            onNamaAyahChange = {},
            onNamaIbuChange = {},
            onUsiaChange = {},
            onLingkarKepalaChange = {},
            onKeteranganChange = {},
            onBeratBadanChange = {},
            onTinggiBadanChange = {},
            onNavigateBack = {},
            onUpdate = {},
            onTakeBerat = {},
            onTakeTinggi = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditBalitaScreenLoadingPreview() {
    EPOSYANDUTheme {
        EditBalitaScreenContent(
            balita = null,
            namaAyah = "",
            namaIbu = "",
            usia = "",
            lingkarKepala = "",
            keterangan = "",
            beratBadan = "",
            tinggiBadan = "",
            sensorData = null,
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            onNamaAyahChange = {},
            onNamaIbuChange = {},
            onUsiaChange = {},
            onLingkarKepalaChange = {},
            onKeteranganChange = {},
            onBeratBadanChange = {},
            onTinggiBadanChange = {},
            onNavigateBack = {},
            onUpdate = {},
            onTakeBerat = {},
            onTakeTinggi = {}
        )
    }
}