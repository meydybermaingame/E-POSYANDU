package com.example.e_posyandu.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.e_posyandu.util.BleConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.util.UUID
import android.os.Build

data class BleReading(
    val beratKg: Double,
    val tinggiCm: Double,
    val timestampMs: Long
)

class BleRepository(private val context: Context) {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var gatt: BluetoothGatt? = null

    private var dataChar: BluetoothGattCharacteristic? = null
    private var commandChar: BluetoothGattCharacteristic? = null

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState

    private val _sensorData = MutableStateFlow<BleReading?>(null)
    val sensorData: StateFlow<BleReading?> = _sensorData

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val name = device.name ?: ""
            if (name == BleConstants.DEVICE_NAME ||
                result.scanRecord?.serviceUuids?.contains(ParcelUuid(BleConstants.SERVICE_UUID)) == true
            ) {
                Log.d("BleRepo", "Found target device: ${device.address}")
                stopScan()
                connect(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BleRepo", "Scan failed: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!hasScanPermission()) return
        if (bluetoothAdapter?.isEnabled != true) {
            Log.w("BleRepo", "Bluetooth is disabled")
            return
        }
        val filters = listOf(
            ScanFilter.Builder().setServiceUuid(ParcelUuid(BleConstants.SERVICE_UUID)).build()
        )
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner = bluetoothAdapter?.bluetoothLeScanner
        scanner?.startScan(filters, settings, scanCallback)
        Log.d("BleRepo", "Start scanning BLE")
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        try {
            scanner?.stopScan(scanCallback)
        } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    private fun connect(device: BluetoothDevice) {
        if (!hasConnectPermission()) return
        gatt = device.connectGatt(context, false, gattCallback)
        Log.d("BleRepo", "Connecting to ${device.address}")
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BleRepo", "Connected, discovering services")
                _connectionState.value = true
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BleRepo", "Disconnected")
                _connectionState.value = false
                dataChar = null
                commandChar = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(BleConstants.SERVICE_UUID)
            if (service != null) {
                dataChar = service.getCharacteristic(BleConstants.DATA_CHAR_UUID)
                commandChar = service.getCharacteristic(BleConstants.COMMAND_CHAR_UUID)
                enableNotifications(gatt, dataChar)
                Log.d("BleRepo", "Service discovered and notifications enabled")
            } else {
                Log.e("BleRepo", "Service not found")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == BleConstants.DATA_CHAR_UUID) {
                val strValue = String(value)
                try {
                    val json = JSONObject(strValue)
                    val berat = json.optDouble("berat", Double.NaN)
                    val tinggi = json.optDouble("tinggi", Double.NaN)
                    val ts = json.optLong("timestamp", System.currentTimeMillis())
                    if (!berat.isNaN() && !tinggi.isNaN()) {
                        _sensorData.value = BleReading(beratKg = berat, tinggiCm = tinggi, timestampMs = ts)
                    }
                } catch (e: Exception) {
                    Log.e("BleRepo", "Invalid JSON: $strValue", e)
                }
            }
        }
        
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // Legacy callback for older Android versions
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (characteristic.uuid == BleConstants.DATA_CHAR_UUID) {
                    @Suppress("DEPRECATION")
                    val value = characteristic.value?.let { String(it) } ?: return
                    try {
                        val json = JSONObject(value)
                        val berat = json.optDouble("berat", Double.NaN)
                        val tinggi = json.optDouble("tinggi", Double.NaN)
                        val ts = json.optLong("timestamp", System.currentTimeMillis())
                        if (!berat.isNaN() && !tinggi.isNaN()) {
                            _sensorData.value = BleReading(beratKg = berat, tinggiCm = tinggi, timestampMs = ts)
                        }
                    } catch (e: Exception) {
                        Log.e("BleRepo", "Invalid JSON: $value", e)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?) {
        if (characteristic == null) return
        if (!hasConnectPermission()) return
        gatt.setCharacteristicNotification(characteristic, true)
        val cccd = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        if (cccd != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(cccd, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                gatt.writeDescriptor(cccd)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendCommand(command: String): Boolean {
        val g = gatt ?: return false
        val ch = commandChar ?: return false
        if (!hasConnectPermission()) return false
        val ok = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = g.writeCharacteristic(ch, command.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            result == 0 // BluetoothStatusCodes.SUCCESS = 0
        } else {
            @Suppress("DEPRECATION")
            ch.value = command.toByteArray()
            @Suppress("DEPRECATION")
            g.writeCharacteristic(ch)
        }
        Log.d("BleRepo", "Write command '$command' result=$ok")
        return ok
    }

    private fun hasScanPermission(): Boolean {
        val p = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
        return p == PackageManager.PERMISSION_GRANTED
    }

    private fun hasConnectPermission(): Boolean {
        val p = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
        return p == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        try {
            stopScan()
            gatt?.disconnect()
            gatt?.close()
            gatt = null
            dataChar = null
            commandChar = null
            _connectionState.value = false
        } catch (_: Exception) {}
    }
}