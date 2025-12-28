package com.example.e_posyandu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_posyandu.data.repository.BalitaRepository
import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.data.repository.Riwayat
import com.example.e_posyandu.data.repository.SensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class BalitaViewModel : ViewModel() {
    private val repository = BalitaRepository()
    
    // State flows
    private val _balitaList = MutableStateFlow<List<Balita>>(emptyList())
    val balitaList: StateFlow<List<Balita>> = _balitaList.asStateFlow()
    
    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    // Connection status
    private val _connectionStatus = MutableStateFlow("disconnected")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()
    
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    init {
        loadBalitaList()
        observeSensorData()
    }

    private fun loadBalitaList() {
        viewModelScope.launch {
            repository.getBalitaList().collect { balitaList ->
                _balitaList.value = balitaList
            }
        }
    }

    private fun observeSensorData() {
        viewModelScope.launch {
            repository.getSensorData().collect { sensorData ->
                _sensorData.value = sensorData
                _connectionStatus.value = sensorData?.connectionStatus ?: "disconnected"
            }
        }
    }

    fun addBalita(balita: Balita) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            val result = repository.addBalita(balita)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Data balita berhasil ditambahkan"
                    clearMessagesAfterDelay()
                },
                onFailure = { exception ->
                    _errorMessage.value = "Gagal menambahkan data: ${exception.message}"
                    clearMessagesAfterDelay()
                }
            )
            _isLoading.value = false
        }
    }

    fun updateBalita(balita: Balita) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            val result = repository.updateBalita(balita)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Data balita berhasil diperbarui"
                    clearMessagesAfterDelay()
                },
                onFailure = { exception ->
                    _errorMessage.value = "Gagal memperbarui data: ${exception.message}"
                    clearMessagesAfterDelay()
                }
            )
            _isLoading.value = false
        }
    }

    fun deleteBalita(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            val result = repository.deleteBalita(id)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Data balita berhasil dihapus"
                    clearMessagesAfterDelay()
                },
                onFailure = { exception ->
                    _errorMessage.value = "Gagal menghapus data: ${exception.message}"
                    clearMessagesAfterDelay()
                }
            )
            _isLoading.value = false
        }
    }

    fun addRiwayat(riwayat: Riwayat) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            val result = repository.addRiwayat(riwayat)
            result.fold(
                onSuccess = {
                    _successMessage.value = "Riwayat berhasil ditambahkan"
                    clearMessagesAfterDelay()
                },
                onFailure = { exception ->
                    _errorMessage.value = "Gagal menambahkan riwayat: ${exception.message}"
                    clearMessagesAfterDelay()
                }
            )
            _isLoading.value = false
        }
    }

    // Real ESP32 connection methods
    fun connectESP32() {
        viewModelScope.launch {
            _isConnecting.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            val result = repository.connectESP32()
            result.fold(
                onSuccess = {
                    _successMessage.value = "ESP32 berhasil terhubung! Sensor siap digunakan."
                    clearMessagesAfterDelay()
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Gagal menghubungkan ESP32"
                    clearMessagesAfterDelay()
                }
            )
            _isConnecting.value = false
        }
    }

    fun disconnectESP32() {
        viewModelScope.launch {
            _isConnecting.value = true
            _errorMessage.value = null
            _successMessage.value = null
            
            val result = repository.disconnectESP32()
            result.fold(
                onSuccess = {
                    _successMessage.value = "ESP32 berhasil diputuskan"
                    clearMessagesAfterDelay()
                },
                onFailure = { exception ->
                    _errorMessage.value = "Gagal memutuskan ESP32: ${exception.message}"
                    clearMessagesAfterDelay()
                }
            )
            _isConnecting.value = false
        }
    }

    // Real sensor data updates will come directly from ESP32 via Firebase
    // No periodic updates needed - Firebase listeners handle real-time data

    private fun clearMessagesAfterDelay() {
        viewModelScope.launch {
            delay(3000)
            _errorMessage.value = null
            _successMessage.value = null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun searchBalita(query: String): List<Balita> {
        return if (query.isEmpty()) {
            _balitaList.value
        } else {
            _balitaList.value.filter { balita ->
                balita.nama.contains(query, ignoreCase = true)
            }
        }
    }
}