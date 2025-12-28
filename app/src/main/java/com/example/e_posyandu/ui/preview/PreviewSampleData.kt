package com.example.e_posyandu.ui.preview

import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.data.repository.Riwayat

/**
 * Sample data provider for Compose Preview.
 * These functions provide static data that can be used in @Preview composables
 * to avoid ViewModel and repository dependencies.
 */
object PreviewSampleData {
    
    val sampleBalitaList = listOf(
        Balita(
            id = "1",
            nama = "Ahmad Fauzi",
            namaAyah = "Budi Santoso",
            namaIbu = "Siti Aminah",
            usia = 2,
            berat = 12.5f,
            tinggi = 85.0f,
            jenisKelamin = "Laki-laki",
            tanggalLahir = "2022-06-15",
            tanggalDaftar = "2024-01-10",
            riwayat = listOf(
                Riwayat(tanggal = "2024-01-10", berat = 12.5f, tinggi = 85.0f),
                Riwayat(tanggal = "2024-02-10", berat = 13.0f, tinggi = 87.0f)
            )
        ),
        Balita(
            id = "2",
            nama = "Siti Nurhaliza",
            namaAyah = "Agus Setiawan",
            namaIbu = "Dewi Kartika",
            usia = 3,
            berat = 14.0f,
            tinggi = 92.0f,
            jenisKelamin = "Perempuan",
            tanggalLahir = "2021-03-20",
            tanggalDaftar = "2024-01-15",
            riwayat = listOf(
                Riwayat(tanggal = "2024-01-15", berat = 14.0f, tinggi = 92.0f)
            )
        ),
        Balita(
            id = "3",
            nama = "Budi Prasetyo",
            namaAyah = "Andi Wijaya",
            namaIbu = "Rina Susanti",
            usia = 1,
            berat = 9.5f,
            tinggi = 75.0f,
            jenisKelamin = "Laki-laki",
            tanggalLahir = "2023-01-10",
            tanggalDaftar = "2024-02-01",
            riwayat = emptyList()
        )
    )
    
    val singleBalita = sampleBalitaList.first()
    
    val emptyBalitaList = emptyList<Balita>()
}
