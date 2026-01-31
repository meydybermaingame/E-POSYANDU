package com.example.e_posyandu.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.ui.component.BalitaItem
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_posyandu.ui.theme.EPOSYANDUTheme
import com.example.e_posyandu.ui.preview.PreviewSampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataBalitaScreen(
    onNavigateToInput: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: BalitaViewModel = viewModel()
) {
    val balitaList by viewModel.balitaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            viewModel.clearMessages()
        }
    }

    DataBalitaScreenContent(
        balitaList = balitaList,
        isLoading = isLoading,
        errorMessage = errorMessage,
        successMessage = successMessage,
        onNavigateToInput = onNavigateToInput,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToEdit = onNavigateToEdit,
        onDeleteBalita = { nama -> viewModel.deleteBalita(nama) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataBalitaScreenContent(
    balitaList: List<Balita>,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onNavigateToInput: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDeleteBalita: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var balitaToDelete by remember { mutableStateOf<Balita?>(null) }
    
    val filteredBalitaList = remember(searchQuery, balitaList) {
        if (searchQuery.isEmpty()) {
            balitaList
        } else {
            balitaList.filter { balita ->
                balita.nama.contains(searchQuery, ignoreCase = true) ||
                balita.namaAyah.contains(searchQuery, ignoreCase = true) ||
                balita.namaIbu.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Data Balita", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    IconButton(onClick = onNavigateToInput) {
                        Icon(
                            Icons.Default.Add, 
                            contentDescription = "Tambah Balita"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF006064),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF006064), Color(0xFF00695C))
                    )
                )
                .padding(paddingValues)
        ) {
            // Search bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Cari balita...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF006064),
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            // Error message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(8.dp),
                        shape = RoundedCornerShape(16.dp),
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(8.dp),
                        shape = RoundedCornerShape(16.dp),
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

            // Content
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            modifier = Modifier.padding(16.dp).shadow(8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF006064), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Memuat data...", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                        }
                    }
                }
                filteredBalitaList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            modifier = Modifier.padding(16.dp).shadow(8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (searchQuery.isEmpty()) Icons.Default.PersonOff else Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (searchQuery.isEmpty()) "Belum ada data balita" else "Tidak ada balita yang ditemukan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                if (searchQuery.isEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = onNavigateToInput,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Tambah Balita Pertama")
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Statistics Card
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${filteredBalitaList.size}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                                Text("Total Balita", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${filteredBalitaList.count { it.usia <= 2 }}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                Text("Usia 0-2 Tahun", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${filteredBalitaList.count { it.usia > 2 }}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                                Text("Usia 3-5 Tahun", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Balita List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredBalitaList) { balita ->
                            BalitaItem(
                                balita = balita,
                                onViewClick = { onNavigateToDetail(balita.nama) },
                                onEditClick = { onNavigateToEdit(balita.nama) },
                                onDeleteClick = {
                                    balitaToDelete = balita
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && balitaToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus data balita '${balitaToDelete?.nama}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        balitaToDelete?.let { onDeleteBalita(it.nama) }
                        showDeleteDialog = false
                        balitaToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336))
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; balitaToDelete = null }) { Text("Batal") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ==================== PREVIEW SECTION ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataBalitaScreenPreview() {
    EPOSYANDUTheme {
        DataBalitaScreenContent(
            balitaList = PreviewSampleData.sampleBalitaList,
            isLoading = false,
            errorMessage = null,
            successMessage = null,
            onNavigateToInput = {},
            onNavigateToDetail = {},
            onNavigateToEdit = {},
            onDeleteBalita = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataBalitaScreenLoadingPreview() {
    EPOSYANDUTheme {
        DataBalitaScreenContent(
            balitaList = emptyList(),
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            onNavigateToInput = {},
            onNavigateToDetail = {},
            onNavigateToEdit = {},
            onDeleteBalita = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataBalitaScreenEmptyPreview() {
    EPOSYANDUTheme {
        DataBalitaScreenContent(
            balitaList = emptyList(),
            isLoading = false,
            errorMessage = null,
            successMessage = null,
            onNavigateToInput = {},
            onNavigateToDetail = {},
            onNavigateToEdit = {},
            onDeleteBalita = {}
        )
    }
}