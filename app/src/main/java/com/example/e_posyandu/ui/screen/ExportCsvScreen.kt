package com.example.e_posyandu.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.preview.PreviewSampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportCsvScreen(
    onNavigateBack: () -> Unit,
    viewModel: BalitaViewModel = viewModel()
) {
    val context = LocalContext.current
    val balitaList by viewModel.balitaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var exportStatus by remember { mutableStateOf<ExportStatus>(ExportStatus.Idle) }
    var exportedFilePath by remember { mutableStateOf<String?>(null) }

    ExportCsvScreenContent(
        balitaList = balitaList,
        isLoading = isLoading,
        exportStatus = exportStatus,
        exportedFilePath = exportedFilePath,
        onNavigateBack = onNavigateBack,
        onExport = {
            exportStatus = ExportStatus.Exporting
            try {
                val filePath = exportBalitaToCsv(context, balitaList)
                exportedFilePath = filePath
                exportStatus = ExportStatus.Success
            } catch (e: Exception) {
                exportStatus = ExportStatus.Error(e.message ?: "Unknown error")
            }
        }
    )
}

sealed class ExportStatus {
    object Idle : ExportStatus()
    object Exporting : ExportStatus()
    object Success : ExportStatus()
    data class Error(val message: String) : ExportStatus()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportCsvScreenContent(
    balitaList: List<Balita>,
    isLoading: Boolean,
    exportStatus: ExportStatus,
    exportedFilePath: String?,
    onNavigateBack: () -> Unit,
    onExport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Data CSV") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF006064),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export Data Balita",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Export semua data balita ke file CSV yang dapat dibuka di Excel atau aplikasi spreadsheet lainnya.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Data summary
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ringkasan Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Data Balita:")
                        Text(
                            text = "${balitaList.size} balita",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Usia 0-2 Tahun:")
                        Text(
                            text = "${balitaList.count { it.usia <= 2 }} balita",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Usia 3-5 Tahun:")
                        Text(
                            text = "${balitaList.count { it.usia > 2 }} balita",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Export status
            when (exportStatus) {
                is ExportStatus.Exporting -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sedang mengeksport data...")
                        }
                    }
                }
                is ExportStatus.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E8)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Export Berhasil!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            if (exportedFilePath != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "File disimpan di:\n$exportedFilePath",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                is ExportStatus.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFF44336)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Error: ${exportStatus.message}",
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.weight(1f))

            // Export button
            Button(
                onClick = onExport,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = balitaList.isNotEmpty() && exportStatus !is ExportStatus.Exporting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006064)
                )
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (exportStatus is ExportStatus.Success) "Export Lagi" else "Export ke CSV",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun exportBalitaToCsv(context: Context, balitaList: List<Balita>): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val fileName = "data_balita_${dateFormat.format(Date())}.csv"
    
    val csvContent = buildString {
        // Header
        appendLine("ID,Nama,Nama Ayah,Nama Ibu,Usia (tahun),Jenis Kelamin,Tanggal Lahir,Berat (kg),Tinggi (cm),Lingkar Kepala (cm),Keterangan,Tanggal Daftar")
        
        // Data rows
        balitaList.forEach { balita ->
            appendLine("${balita.id},${balita.nama},${balita.namaAyah},${balita.namaIbu},${balita.usia},${balita.jenisKelamin},${balita.tanggalLahir},${balita.berat},${balita.tinggi},${balita.lingkarKepala},${balita.keterangan},${balita.tanggalDaftar}")
        }
    }
    
    val file = File(context.getExternalFilesDir(null), fileName)
    FileOutputStream(file).use { it.write(csvContent.toByteArray()) }
    
    return file.absolutePath
}

// ==================== PREVIEW SECTION ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExportCsvScreenPreview() {
    EPOSYANDUTheme {
        ExportCsvScreenContent(
            balitaList = PreviewSampleData.sampleBalitaList,
            isLoading = false,
            exportStatus = ExportStatus.Idle,
            exportedFilePath = null,
            onNavigateBack = {},
            onExport = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExportCsvScreenSuccessPreview() {
    EPOSYANDUTheme {
        ExportCsvScreenContent(
            balitaList = PreviewSampleData.sampleBalitaList,
            isLoading = false,
            exportStatus = ExportStatus.Success,
            exportedFilePath = "/storage/emulated/0/data_balita.csv",
            onNavigateBack = {},
            onExport = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExportCsvScreenEmptyPreview() {
    EPOSYANDUTheme {
        ExportCsvScreenContent(
            balitaList = emptyList(),
            isLoading = false,
            exportStatus = ExportStatus.Idle,
            exportedFilePath = null,
            onNavigateBack = {},
            onExport = {}
        )
    }
}