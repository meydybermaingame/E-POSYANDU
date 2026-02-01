package com.example.e_posyandu.ui.screen

import com.example.e_posyandu.data.model.toRiwayatModelList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.ui.component.KmsChart
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PertumbuhanScreen(
    onNavigateBack: () -> Unit,
    viewModel: BalitaViewModel = viewModel()
) {
    val balitaList by viewModel.balitaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    PertumbuhanScreenContent(
        balitaList = balitaList,
        isLoading = isLoading,
        onNavigateBack = onNavigateBack
    )
}

// ==================== STATELESS CONTENT FOR PREVIEW ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PertumbuhanScreenContent(
    balitaList: List<Balita>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBalita by remember { mutableStateOf<Balita?>(null) }
    
    val filteredBalitaList = remember(searchQuery, balitaList) {
        if (searchQuery.isEmpty()) {
            balitaList
        } else {
            balitaList.filter { balita ->
                balita.nama.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pertumbuhan Balita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari balita untuk melihat pertumbuhan...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredBalitaList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) {
                            "Belum ada data balita"
                        } else {
                            "Tidak ada balita yang ditemukan"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredBalitaList) { balita ->
                        BalitaGrowthCard(
                            balita = balita,
                            isSelected = selectedBalita?.nama == balita.nama,
                            onSelect = { selectedBalita = balita }
                        )
                    }
                }
            }
        }
    }

    // Fullscreen chart view instead of dialog
    selectedBalita?.let { balita ->
        FullscreenChartView(
            balita = balita,
            onDismiss = { selectedBalita = null }
        )
    }
}

/**
 * Fullscreen chart view optimized for landscape
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenChartView(
    balita: Balita,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grafik Pertumbuhan ${balita.nama}") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tutup")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLandscape) {
            // Landscape: Chart fills entire screen with minimal padding
            KmsChart(
                riwayatList = balita.riwayat.toRiwayatModelList(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surface),
                height = null, // Use all available space
                title = "", // Hide title in landscape to maximize space
                jenisKelamin = if (balita.jenisKelamin.contains("Laki", ignoreCase = true)) "L" else "P",
                tanggalLahir = balita.tanggalLahir
            )
        } else {
            // Portrait: Scrollable with info card + chart
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Balita info card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Informasi Balita",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nama: ${balita.nama}")
                        Text("Usia: ${balita.usia} tahun")
                        Text("Jenis Kelamin: ${balita.jenisKelamin}")
                        Text("Berat Terakhir: ${balita.berat} kg")
                        Text("Tinggi Terakhir: ${balita.tinggi} cm")
                        Text("Total Riwayat: ${balita.riwayat.size} data")
                    }
                }
                
                // Growth chart
                KmsChart(
                    riwayatList = balita.riwayat.toRiwayatModelList(),
                    height = 500.dp,  // Fixed height for portrait
                    title = "Grafik Pertumbuhan KMS",
                    jenisKelamin = if (balita.jenisKelamin.contains("Laki", ignoreCase = true)) "L" else "P",
                    tanggalLahir = balita.tanggalLahir
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BalitaGrowthCard(
    balita: Balita,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = balita.nama,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Usia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${balita.usia} tahun",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "Berat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${balita.berat} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "Tinggi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${balita.tinggi} cm",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "Riwayat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${balita.riwayat.size} data",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Klik untuk melihat grafik pertumbuhan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ==================== PREVIEW SECTION ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PertumbuhanScreenPreview() {
    EPOSYANDUTheme {
        // Sample data for preview
        val sampleBalitaList = listOf(
            Balita(
                nama = "Ahmad Fauzi",
                usia = 2,
                berat = 12.5f,
                tinggi = 85.0f,
                jenisKelamin = "Laki-laki",
                tanggalLahir = "2022-06-15",
                riwayat = emptyList()
            ),
            Balita(
                nama = "Siti Nurhaliza",
                usia = 3,
                berat = 14.0f,
                tinggi = 92.0f,
                jenisKelamin = "Perempuan",
                tanggalLahir = "2021-03-20",
                riwayat = emptyList()
            ),
            Balita(
                nama = "Budi Santoso",
                usia = 1,
                berat = 9.5f,
                tinggi = 75.0f,
                jenisKelamin = "Laki-laki",
                tanggalLahir = "2023-01-10",
                riwayat = emptyList()
            )
        )
        
        PertumbuhanScreenContent(
            balitaList = sampleBalitaList,
            isLoading = false,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PertumbuhanScreenLoadingPreview() {
    EPOSYANDUTheme {
        PertumbuhanScreenContent(
            balitaList = emptyList(),
            isLoading = true,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PertumbuhanScreenEmptyPreview() {
    EPOSYANDUTheme {
        PertumbuhanScreenContent(
            balitaList = emptyList(),
            isLoading = false,
            onNavigateBack = {}
        )
    }
}