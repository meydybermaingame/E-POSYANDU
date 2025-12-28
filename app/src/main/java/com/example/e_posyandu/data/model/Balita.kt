package com.example.e_posyandu.data.model

import com.example.e_posyandu.data.repository.Balita as RepositoryBalita
import com.example.e_posyandu.data.repository.Riwayat as RepositoryRiwayat

data class Balita(
    val nama: String = "",
    val namaAyah: String = "",
    val namaIbu: String = "",
    val usia: Int = 0,
    val lingkarKepala: Float = 0f,
    val keterangan: String = "",
    val berat: Float = 0f,
    val tinggi: Float = 0f,
    val riwayat: List<Riwayat> = emptyList(),
    val tanggalDaftar: String = "",
    val jenisKelamin: String = "",
    val tanggalLahir: String = ""
)

data class Riwayat(
    val tanggal: String = "",
    val berat: Float = 0f,
    val tinggi: Float = 0f
)

data class SensorData(
    val berat: Float = 0f,
    val tinggi: Float = 0f
)

// Adapter functions to convert between repository and model classes
fun RepositoryBalita.toModel(): Balita {
    return Balita(
        nama = this.nama,
        namaAyah = this.namaAyah,
        namaIbu = this.namaIbu,
        usia = this.usia,
        lingkarKepala = this.lingkarKepala,
        keterangan = this.keterangan,
        berat = this.berat,
        tinggi = this.tinggi,
        riwayat = this.riwayat.map { it.toModel() },
        tanggalDaftar = this.tanggalDaftar,
        jenisKelamin = this.jenisKelamin,
        tanggalLahir = this.tanggalLahir
    )
}

fun RepositoryRiwayat.toModel(): Riwayat {
    return Riwayat(
        tanggal = this.tanggal,
        berat = this.berat,
        tinggi = this.tinggi
    )
}

fun List<RepositoryBalita>.toModelList(): List<Balita> {
    return this.map { it.toModel() }
}

fun List<RepositoryRiwayat>.toRiwayatModelList(): List<Riwayat> {
    return this.map { it.toModel() }
}