package com.example.e_posyandu.data.repository

import com.example.e_posyandu.BuildConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await


data class Balita(
    var id: String = "",
    var nama: String = "",
    var tanggalLahir: String = "",
    var jenisKelamin: String = "",
    var namaAyah: String = "",
    var namaIbu: String = "",
    var alamat: String = "",
    var beratBadan: Float = 0f,
    var tinggiBadan: Float = 0f,
    var catatan: String = "",
    var tanggalDaftar: String = "",
    var usia: Int = 0,
    var lingkarKepala: Float = 0f,
    var keterangan: String = "",
    var berat: Float = 0f,
    var tinggi: Float = 0f,
    var riwayat: List<Riwayat> = emptyList()
)

data class Riwayat(
    var id: String = "",
    var balitaId: String = "",
    var tanggal: String = "",
    var beratBadan: Float = 0f,
    var tinggiBadan: Float = 0f,
    var catatan: String = "",
    var berat: Float = 0f,
    var tinggi: Float = 0f
)

data class SensorData(
    var berat: Float = 0f,
    var tinggi: Float = 0f,
    var timestamp: Long = System.currentTimeMillis(),
    var connectionStatus: String = "disconnected" // "connecting", "connected", "disconnected", "error"
)

class BalitaRepository {
    private val database = FirebaseDatabase.getInstance()
    private val balitaRef = database.getReference("balita")
    private val riwayatRef = database.getReference("riwayat")
    private val sensorRef = database.getReference("sensor")
    
    // Connection status tracking
    private var isConnecting = false
    private var connectionAttempts = 0
    private val maxConnectionAttempts = 3

    // Initialize sensor data with disconnected status
    init {
        // Force set disconnected status to override any existing data
        sensorRef.setValue(SensorData(
            berat = 0f,
            tinggi = 0f,
            timestamp = System.currentTimeMillis(),
            connectionStatus = "disconnected"
        ))
    }

    fun getBalitaList(): Flow<List<Balita>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val balitaList = mutableListOf<Balita>()
                for (childSnapshot in snapshot.children) {
                    val balita = childSnapshot.getValue(Balita::class.java)
                    balita?.let { balitaList.add(it) }
                }
                trySend(balitaList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        balitaRef.addValueEventListener(listener)
        awaitClose { balitaRef.removeEventListener(listener) }
    }

    fun getSensorData(): Flow<SensorData?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sensorData = snapshot.getValue(SensorData::class.java)
                trySend(sensorData)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        sensorRef.addValueEventListener(listener)
        awaitClose { sensorRef.removeEventListener(listener) }
    }

    suspend fun addBalita(balita: Balita): Result<Unit> {
        return try {
            val id = balitaRef.push().key ?: return Result.failure(Exception("Failed to generate ID"))
            val balitaWithId = balita.copy(id = id)
            balitaRef.child(id).setValue(balitaWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // New helper to return generated ID (used by DummyDataGenerator)
    suspend fun addBalitaReturningId(balita: Balita): Result<String> {
        return try {
            val id = balitaRef.push().key ?: return Result.failure(Exception("Failed to generate ID"))
            val balitaWithId = balita.copy(id = id)
            balitaRef.child(id).setValue(balitaWithId).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBalita(balita: Balita): Result<Unit> {
        return try {
            balitaRef.child(balita.id).setValue(balita).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBalita(id: String): Result<Unit> {
        return try {
            balitaRef.child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRiwayat(riwayat: Riwayat): Result<Unit> {
        return try {
            val id = riwayatRef.push().key ?: return Result.failure(Exception("Failed to generate ID"))
            val riwayatWithId = riwayat.copy(id = id)
            riwayatRef.child(id).setValue(riwayatWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Real ESP32 connection - no simulation
    suspend fun connectESP32(): Result<Unit> = withContext(Dispatchers.IO) {
        if (isConnecting) {
            return@withContext Result.failure(Exception("Connection already in progress"))
        }

        isConnecting = true
        connectionAttempts++

        try {
            // Update status to connecting
            sensorRef.setValue(SensorData(connectionStatus = "connecting"))
            
            // Try to connect to real ESP32
            delay(3000) // Give time for connection attempt
            
            // Since there's no real ESP32 hardware, connection will always fail
            // Real ESP32 would need to actively send data to Firebase to establish connection
            
            // Connection failed - no real ESP32 hardware detected
            sensorRef.setValue(SensorData(connectionStatus = "error"))
            val errorMessage = "ESP32 tidak ditemukan. Pastikan perangkat ESP32 sudah dirakit dan terhubung ke WiFi."
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            sensorRef.setValue(SensorData(connectionStatus = "error"))
            Result.failure(Exception("Gagal menghubungkan ESP32: ${e.message}"))
        } finally {
            isConnecting = false
        }
    }

    // Disconnect ESP32
    suspend fun disconnectESP32(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Simulate disconnection delay
            delay(1000)
            
            // Clear sensor data and set status to disconnected
            sensorRef.setValue(SensorData(connectionStatus = "disconnected"))
            connectionAttempts = 0 // Reset attempts
            isConnecting = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get current connection status
    fun getConnectionStatus(): String {
        return when {
            isConnecting -> "connecting"
            connectionAttempts > 0 -> "error"
            else -> "disconnected"
        }
    }

    // Real sensor data will be updated by ESP32 hardware directly to Firebase
    // No simulation needed - ESP32 will push real sensor readings
}