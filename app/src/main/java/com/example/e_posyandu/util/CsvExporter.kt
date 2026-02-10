package com.example.e_posyandu.util

import android.content.Context
import android.os.Environment
import com.example.e_posyandu.data.model.Balita
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class CsvExporter {
    companion object {
        fun exportBalitaToCsv(context: Context, balitaList: List<Balita>): File? {
            return try {
                val fileName = "data_balita_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)
                
                val writer = CSVWriter(FileWriter(file))
                
                // Header
                writer.writeNext(arrayOf(
                    "Nama Balita",
                    "Nama Ayah",
                    "Nama Ibu",
                    "Usia",
                    "Lingkar Kepala",
                    "Keterangan",
                    "Berat Badan",
                    "Tinggi Badan",
                    "Tanggal Daftar"
                ))
                
                // Data balita
                balitaList.forEach { balita ->
                    writer.writeNext(arrayOf(
                        balita.nama,
                        balita.namaAyah,
                        balita.namaIbu,
                        balita.usia.toString(),
                        balita.lingkarKepala.toString(),
                        balita.keterangan,
                        balita.berat.toString(),
                        balita.tinggi.toString(),
                        balita.tanggalDaftar
                    ))
                }
                
                // Riwayat pertumbuhan
                if (balitaList.any { it.riwayat.isNotEmpty() }) {
                    writer.writeNext(arrayOf()) // Empty line
                    writer.writeNext(arrayOf("RIWAYAT PERTUMBUHAN"))
                    writer.writeNext(arrayOf(
                        "Nama Balita",
                        "Tanggal",
                        "Berat Badan",
                        "Tinggi Badan"
                    ))
                    
                    balitaList.forEach { balita ->
                        balita.riwayat.forEach { riwayat ->
                            writer.writeNext(arrayOf(
                                balita.nama,
                                riwayat.tanggal,
                                riwayat.berat.toString(),
                                riwayat.tinggi.toString()
                            ))
                        }
                    }
                }
                
                writer.close()
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
} 