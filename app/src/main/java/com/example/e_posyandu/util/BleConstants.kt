package com.example.e_posyandu.util

import java.util.UUID

object BleConstants {
    // Samakan dengan UUID di ESP32
    val SERVICE_UUID: UUID = UUID.fromString("e8c15bd0-5f6d-4f0c-9f2a-1a2b3c4d5e60")
    val DATA_CHAR_UUID: UUID = UUID.fromString("e8c15bd1-5f6d-4f0c-9f2a-1a2b3c4d5e60")
    val COMMAND_CHAR_UUID: UUID = UUID.fromString("e8c15bd2-5f6d-4f0c-9f2a-1a2b3c4d5e60")

    const val DEVICE_NAME = "E-POSYANDU"
}