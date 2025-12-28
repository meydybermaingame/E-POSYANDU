package com.example.e_posyandu.utils

import com.example.e_posyandu.data.repository.Balita
import com.example.e_posyandu.data.repository.BalitaRepository
import com.example.e_posyandu.data.repository.Riwayat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

class DummyDataGenerator {
    private val repository = BalitaRepository()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    fun generateDummyData() {
        Log.d("DummyDataGenerator", "🚀 Memulai generate dummy data...")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Data balita dummy dengan riwayat pertumbuhan
                val dummyBalitaList = listOf(
                    createBalitaWithHistory(
                        nama = "Andi Pratama",
                        namaAyah = "Budi Pratama",
                        namaIbu = "Sari Dewi",
                        jenisKelamin = "Laki-laki",
                        tanggalLahir = "15/03/2022",
                        alamat = "Jl. Merdeka No. 123, Jakarta",
                        usia = 2,
                        lingkarKepala = 48.5f,
                        keterangan = "Sehat, aktif",
                        currentBerat = 12.5f,
                        currentTinggi = 85.0f
                    ),
                    createBalitaWithHistory(
                        nama = "Siti Nurhaliza",
                        namaAyah = "Ahmad Yusuf",
                        namaIbu = "Fatimah Zahra",
                        jenisKelamin = "Perempuan",
                        tanggalLahir = "08/07/2021",
                        alamat = "Jl. Sudirman No. 456, Bandung",
                        usia = 3,
                        lingkarKepala = 49.2f,
                        keterangan = "Sehat, ceria",
                        currentBerat = 14.2f,
                        currentTinggi = 92.0f
                    ),
                    createBalitaWithHistory(
                        nama = "Rizky Maulana",
                        namaAyah = "Dedi Kurniawan",
                        namaIbu = "Rina Sari",
                        jenisKelamin = "Laki-laki",
                        tanggalLahir = "22/11/2023",
                        alamat = "Jl. Diponegoro No. 789, Surabaya",
                        usia = 1,
                        lingkarKepala = 46.8f,
                        keterangan = "Sehat, mulai berjalan",
                        currentBerat = 9.8f,
                        currentTinggi = 75.0f
                    ),
                    createBalitaWithHistory(
                        nama = "Maya Cantika",
                        namaAyah = "Hendra Wijaya",
                        namaIbu = "Lestari Indah",
                        jenisKelamin = "Perempuan",
                        tanggalLahir = "05/01/2022",
                        alamat = "Jl. Gatot Subroto No. 321, Yogyakarta",
                        usia = 2,
                        lingkarKepala = 47.9f,
                        keterangan = "Sehat, pandai bicara",
                        currentBerat = 11.8f,
                        currentTinggi = 83.5f
                    ),
                    createBalitaWithHistory(
                        nama = "Farhan Alif",
                        namaAyah = "Irwan Setiawan",
                        namaIbu = "Dewi Kartika",
                        jenisKelamin = "Laki-laki",
                        tanggalLahir = "18/09/2020",
                        alamat = "Jl. Ahmad Yani No. 654, Medan",
                        usia = 4,
                        lingkarKepala = 50.1f,
                        keterangan = "Sehat, siap masuk TK",
                        currentBerat = 16.5f,
                        currentTinggi = 98.0f
                    )
                )
                
                // Tambahkan setiap balita ke database
                dummyBalitaList.forEach { balita ->
                    val addResult = repository.addBalitaReturningId(balita)
                    if (addResult.isSuccess) {
                        val generatedId = addResult.getOrNull() ?: ""
                         println("✅ Berhasil menambahkan data balita: ${balita.nama} dengan id=$generatedId")
                         Log.d("DummyDataGenerator", "✅ Berhasil menambahkan data balita: ${balita.nama} dengan id=$generatedId")

                        // Tambahkan riwayat pertumbuhan untuk setiap balita, kaitkan ke balitaId yang benar
                        balita.riwayat.forEach { r ->
                            val riwayatResult = repository.addRiwayat(r.copy(balitaId = generatedId))
                            if (riwayatResult.isSuccess) {
                                println("   📊 Riwayat ${r.tanggal} ditambahkan")
                            } else {
                                println("   ❌ Gagal menambahkan riwayat ${r.tanggal}: ${riwayatResult.exceptionOrNull()?.message}")
                            }
                        }
                    } else {
                         println("❌ Gagal menambahkan data balita: ${balita.nama}: ${addResult.exceptionOrNull()?.message}")
                         Log.e("DummyDataGenerator", "❌ Gagal menambahkan data balita: ${balita.nama}: ${addResult.exceptionOrNull()?.message}")
                     }
                }
                
                println("\n🎉 Selesai! Data dummy balita telah ditambahkan ke database.")
                 println("📱 Silakan buka aplikasi untuk melihat data balita dan grafik pertumbuhan.")
                 Log.d("DummyDataGenerator", "🎉 Selesai! Data dummy balita telah ditambahkan ke database.")
                 
             } catch (e: Exception) {
                 println("❌ Error saat menambahkan data dummy: ${e.message}")
                 Log.e("DummyDataGenerator", "❌ Error saat menambahkan data dummy: ${e.message}")
             }
        }
    }

    private fun createBalitaWithHistory(
        nama: String,
        namaAyah: String,
        namaIbu: String,
        jenisKelamin: String,
        tanggalLahir: String,
        alamat: String,
        usia: Int,
        lingkarKepala: Float,
        keterangan: String,
        currentBerat: Float,
        currentTinggi: Float
    ): Balita {
        val riwayatList = generateRiwayatPertumbuhan(usia, currentBerat, currentTinggi)
        
        return Balita(
            id = "", // Akan diisi setelah push
            nama = nama,
            tanggalLahir = tanggalLahir,
            jenisKelamin = jenisKelamin,
            namaAyah = namaAyah,
            namaIbu = namaIbu,
            alamat = alamat,
            beratBadan = currentBerat,
            tinggiBadan = currentTinggi,
            catatan = "Data dummy untuk testing aplikasi",
            tanggalDaftar = dateFormat.format(Date()),
            usia = usia,
            lingkarKepala = lingkarKepala,
            keterangan = keterangan,
            berat = currentBerat,
            tinggi = currentTinggi,
            riwayat = riwayatList
        )
    }

    private fun generateRiwayatPertumbuhan(usia: Int, currentBerat: Float, currentTinggi: Float): List<Riwayat> {
        val riwayatList = mutableListOf<Riwayat>()
        val calendar = Calendar.getInstance()
        
        // Generate data pertumbuhan 6 bulan ke belakang
        for (i in 6 downTo 1) {
            calendar.add(Calendar.MONTH, -1)
            val berat = (currentBerat - (0.1f..0.5f).random()).coerceAtLeast(5f)
            val tinggi = (currentTinggi - (0.5f..1.5f).random()).coerceAtLeast(50f)
            
            riwayatList.add(
                Riwayat(
                    id = "",
                    balitaId = "",
                    tanggal = dateFormat.format(calendar.time),
                    beratBadan = berat,
                    tinggiBadan = tinggi,
                    catatan = "Pemeriksaan rutin bulan ${dateFormat.format(calendar.time)}",
                    berat = berat,
                    tinggi = tinggi
                )
            )
        }
        
        // Tambahkan data terkini
        riwayatList.add(
            Riwayat(
                id = "",
                balitaId = "",
                tanggal = dateFormat.format(Date()),
                beratBadan = currentBerat,
                tinggiBadan = currentTinggi,
                catatan = "Data terkini",
                berat = currentBerat,
                tinggi = currentTinggi
            )
        )
        
        return riwayatList.sortedBy { it.tanggal }
    }
}

// Extension function untuk range random float
fun ClosedFloatingPointRange<Float>.random() = 
    Random().nextFloat() * (endInclusive - start) + start