package com.example.e_posyandu.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.e_posyandu.data.model.Balita
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ExcelExporter {
    
    fun exportBalitaToExcel(context: Context, balitaList: List<Balita>): File {
        return try {
            Log.d("ExcelExporter", "Memulai export Excel dengan ${balitaList.size} data balita")
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "laporan_balita_$timestamp.xlsx"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
                Log.d("ExcelExporter", "Membuat direktori Downloads")
            }
            
            val file = File(downloadsDir, fileName)
            Log.d("ExcelExporter", "Membuat file: ${file.absolutePath}")
            
            // Create workbook
            val workbook = XSSFWorkbook()
            
            try {
                // Create summary sheet
                createSummarySheet(workbook, balitaList)
                
                // Create growth history sheet
                createGrowthHistorySheet(workbook, balitaList)
                
                // Write to file
                FileOutputStream(file).use { outputStream ->
                    workbook.write(outputStream)
                }
                
            } finally {
                workbook.close()
            }
            
            Log.d("ExcelExporter", "Export Excel berhasil: ${file.absolutePath}")
            file
            
        } catch (e: Exception) {
            Log.e("ExcelExporter", "Error saat export Excel: ${e.message}", e)
            throw e
        }
    }
    
    private fun createSummarySheet(workbook: XSSFWorkbook, balitaList: List<Balita>) {
        val sheet = workbook.createSheet("Data Balita")
        
        // Simple header style
        val headerStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont().apply {
                bold = true
            }
            setFont(font)
        }
        
        // Simple title
        val titleRow = sheet.createRow(0)
        titleRow.createCell(0).setCellValue("Data Balita E-Posyandu")
        
        // Complete header with available balita data fields
        val headerRow = sheet.createRow(2)
        val headers = arrayOf(
            "No", "Nama Balita", "Nama Ayah", "Nama Ibu", "Usia (tahun)", 
            "Berat (kg)", "Tinggi (cm)", "Lingkar Kepala (cm)", 
            "Keterangan", "Tanggal Daftar"
        )
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        // Complete data content with available balita information
        balitaList.forEachIndexed { index, balita ->
            val row = sheet.createRow(3 + index)
            row.createCell(0).setCellValue((index + 1).toString())
            row.createCell(1).setCellValue(balita.nama)
            row.createCell(2).setCellValue(balita.namaAyah.ifEmpty { "-" })
            row.createCell(3).setCellValue(balita.namaIbu.ifEmpty { "-" })
            row.createCell(4).setCellValue(balita.usia.toString())
            row.createCell(5).setCellValue(String.format("%.1f", balita.berat))
            row.createCell(6).setCellValue(String.format("%.1f", balita.tinggi))
            row.createCell(7).setCellValue(String.format("%.1f", balita.lingkarKepala))
            row.createCell(8).setCellValue(balita.keterangan.ifEmpty { "-" })
            row.createCell(9).setCellValue(balita.tanggalDaftar.ifEmpty { "-" })
        }
        
        // Set manual column widths to avoid AWT FontRenderContext issues
        sheet.setColumnWidth(0, 1500)  // No
        sheet.setColumnWidth(1, 4000)  // Nama Balita
        sheet.setColumnWidth(2, 3500)  // Nama Ayah
        sheet.setColumnWidth(3, 3500)  // Nama Ibu
        sheet.setColumnWidth(4, 2000)  // Usia
        sheet.setColumnWidth(5, 2000)  // Berat
        sheet.setColumnWidth(6, 2000)  // Tinggi
        sheet.setColumnWidth(7, 3000)  // Lingkar Kepala
        sheet.setColumnWidth(8, 4000)  // Keterangan
        sheet.setColumnWidth(9, 3000)  // Tanggal Daftar
    }
    
    private fun createGrowthHistorySheet(workbook: XSSFWorkbook, balitaList: List<Balita>) {
        val sheet = workbook.createSheet("Riwayat Pertumbuhan")
        
        // Header style
        val headerStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont().apply {
                bold = true
            }
            setFont(font)
        }
        
        // Title
        val titleRow = sheet.createRow(0)
        titleRow.createCell(0).setCellValue("Riwayat Pertumbuhan Balita")
        
        // Headers
        val headerRow = sheet.createRow(2)
        val headers = arrayOf(
            "Nama Balita", "Tanggal", "Berat (kg)", "Tinggi (cm)", "Usia (bulan)"
        )
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        // Data riwayat pertumbuhan
        var currentRow = 3
        balitaList.forEach { balita ->
            if (balita.riwayat.isNotEmpty()) {
                balita.riwayat.forEach { riwayat ->
                    val row = sheet.createRow(currentRow++)
                    row.createCell(0).setCellValue(balita.nama)
                    row.createCell(1).setCellValue(riwayat.tanggal)
                    row.createCell(2).setCellValue(String.format("%.1f", riwayat.berat))
                    row.createCell(3).setCellValue(String.format("%.1f", riwayat.tinggi))
                    // Calculate age in months from date if needed
                    row.createCell(4).setCellValue("-")
                }
            } else {
                // If no history, show current data
                val row = sheet.createRow(currentRow++)
                row.createCell(0).setCellValue(balita.nama)
                row.createCell(1).setCellValue(balita.tanggalDaftar)
                row.createCell(2).setCellValue(String.format("%.1f", balita.berat))
                row.createCell(3).setCellValue(String.format("%.1f", balita.tinggi))
                row.createCell(4).setCellValue("${balita.usia * 12}")
            }
        }
        
        // Set column widths
        sheet.setColumnWidth(0, 4000)  // Nama Balita
        sheet.setColumnWidth(1, 3000)  // Tanggal
        sheet.setColumnWidth(2, 2500)  // Berat
        sheet.setColumnWidth(3, 2500)  // Tinggi
        sheet.setColumnWidth(4, 2500)  // Usia
    }

}