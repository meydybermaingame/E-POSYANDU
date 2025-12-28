package com.example.e_posyandu.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.data.model.toRiwayatModelList
import com.example.e_posyandu.ui.component.KmsChart
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import com.example.e_posyandu.ui.preview.PreviewSampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBalitaScreen(
    namaBalita: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: BalitaViewModel = viewModel()
) {
    val balitaList by viewModel.balitaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val balita = remember(namaBalita, balitaList) {
        balitaList.find { it.nama == namaBalita }
    }

    DetailBalitaScreenContent(
        balita = balita,
        isLoading = isLoading,
        onNavigateBack = onNavigateBack,
        onNavigateToEdit = onNavigateToEdit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailBalitaScreenContent(
    balita: Balita?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Balita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            balita == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Data balita tidak ditemukan")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Detail Data Balita",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Basic info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Informasi Dasar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            DetailRow("Nama Balita", balita.nama)
                            DetailRow("Nama Ayah", balita.namaAyah.ifEmpty { "-" })
                            DetailRow("Nama Ibu", balita.namaIbu.ifEmpty { "-" })
                            DetailRow("Usia", "${balita.usia} tahun")
                            DetailRow("Lingkar Kepala", "${balita.lingkarKepala} cm")
                            if (balita.keterangan.isNotEmpty()) {
                                DetailRow("Keterangan", balita.keterangan)
                            }
                            DetailRow("Tanggal Daftar", balita.tanggalDaftar.ifEmpty { "-" })
                        }
                    }

                    // Growth data card
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
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                GrowthDataItem("Berat Badan", "${balita.berat} kg")
                                GrowthDataItem("Tinggi Badan", "${balita.tinggi} cm")
                            }
                        }
                    }

                    // Growth history
                    if (balita.riwayat.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Riwayat Pertumbuhan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                balita.riwayat.sortedBy { it.tanggal }.forEach { riwayat ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = riwayat.tanggal,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                Text("${riwayat.berat} kg")
                                                Text("${riwayat.tinggi} cm")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Growth chart
                        KmsChart(
                            riwayatList = balita.riwayat.toRiwayatModelList(),
                            title = "Grafik Pertumbuhan KMS ${balita.nama}",
                            jenisKelamin = if (balita.jenisKelamin.contains("Laki", ignoreCase = true)) "L" else "P",
                            tanggalLahir = balita.tanggalLahir
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada riwayat pertumbuhan",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun GrowthDataItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== PREVIEW SECTION ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailBalitaScreenPreview() {
    EPOSYANDUTheme {
        DetailBalitaScreenContent(
            balita = PreviewSampleData.singleBalita,
            isLoading = false,
            onNavigateBack = {},
            onNavigateToEdit = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailBalitaScreenLoadingPreview() {
    EPOSYANDUTheme {
        DetailBalitaScreenContent(
            balita = null,
            isLoading = true,
            onNavigateBack = {},
            onNavigateToEdit = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailBalitaScreenNotFoundPreview() {
    EPOSYANDUTheme {
        DetailBalitaScreenContent(
            balita = null,
            isLoading = false,
            onNavigateBack = {},
            onNavigateToEdit = {}
        )
    }
}