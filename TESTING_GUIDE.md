# Panduan Testing Aplikasi Posyandu

Panduan lengkap untuk testing semua fitur aplikasi Posyandu.

## 🧪 Test Cases

### 1. **Input Data Balita**

#### Test Case 1.1: Input Data Lengkap
**Prerequisites:** Aplikasi terbuka, koneksi internet aktif
**Steps:**
1. Buka tab "Input"
2. Isi semua field wajib:
   - Nama Balita: "Ahmad Fadillah"
   - Usia: "3"
   - Berat Badan: "12.5"
   - Tinggi Badan: "95.0"
3. Isi field opsional:
   - Nama Ayah: "Budi Santoso"
   - Nama Ibu: "Siti Aminah"
   - Lingkar Kepala: "48.5"
   - Keterangan: "Balita sehat"
4. Klik "Daftar Balita"

**Expected Result:**
- Data berhasil disimpan
- Muncul pesan sukses
- Form ter-reset
- Data muncul di tab "Data Balita"

#### Test Case 1.2: Input Data Minimal
**Steps:**
1. Isi hanya field wajib:
   - Nama Balita: "Sari Putri"
   - Usia: "2"
   - Berat Badan: "10.0"
   - Tinggi Badan: "85.0"
2. Klik "Daftar Balita"

**Expected Result:**
- Data berhasil disimpan
- Field opsional terisi default value

#### Test Case 1.3: Validasi Input
**Steps:**
1. Coba submit form kosong
2. Coba input usia negatif
3. Coba input berat/tinggi negatif
4. Coba input nama kosong

**Expected Result:**
- Button "Daftar" disabled
- Error message muncul
- Data tidak tersimpan

#### Test Case 1.4: Integrasi Sensor IoT
**Prerequisites:** ESP32 terhubung, data sensor tersedia di Firebase
**Steps:**
1. Pastikan ada data sensor di Firebase
2. Klik "Ambil Berat"
3. Klik "Ambil Tinggi"

**Expected Result:**
- Field berat/tinggi terisi otomatis
- Data sesuai dengan sensor

### 2. **Data Balita**

#### Test Case 2.1: Tampilan Daftar
**Steps:**
1. Buka tab "Data Balita"
2. Pastikan ada data balita

**Expected Result:**
- Daftar balita ditampilkan dalam card
- Informasi lengkap: nama, usia, berat, tinggi
- Tombol aksi: lihat, edit, hapus

#### Test Case 2.2: Pencarian
**Steps:**
1. Masukkan keyword di search bar
2. Test pencarian dengan nama lengkap
3. Test pencarian dengan nama parsial
4. Test pencarian dengan nama ayah/ibu

**Expected Result:**
- Hasil pencarian sesuai keyword
- Real-time filtering
- Empty state jika tidak ada hasil

#### Test Case 2.3: Hapus Data
**Steps:**
1. Klik tombol hapus pada salah satu balita
2. Konfirmasi penghapusan
3. Verifikasi data terhapus

**Expected Result:**
- Dialog konfirmasi muncul
- Data terhapus dari Firebase
- Daftar ter-update
- Pesan sukses muncul

### 3. **Detail Balita**

#### Test Case 3.1: Tampilan Detail
**Steps:**
1. Klik tombol lihat pada balita
2. Periksa semua informasi

**Expected Result:**
- Informasi lengkap ditampilkan
- Layout rapi dan mudah dibaca
- Tombol edit tersedia

#### Test Case 3.2: Riwayat Pertumbuhan
**Prerequisites:** Balita memiliki riwayat pertumbuhan
**Steps:**
1. Buka detail balita dengan riwayat
2. Periksa daftar riwayat

**Expected Result:**
- Riwayat ditampilkan secara kronologis
- Data lengkap: tanggal, berat, tinggi

### 4. **Edit Data Balita**

#### Test Case 4.1: Edit Data
**Steps:**
1. Klik tombol edit pada balita
2. Ubah beberapa field
3. Klik "Update Data"

**Expected Result:**
- Form terisi dengan data lama
- Nama balita tidak bisa diubah
- Data berhasil diupdate
- Kembali ke halaman sebelumnya

#### Test Case 4.2: Validasi Edit
**Steps:**
1. Coba hapus field wajib
2. Coba input nilai invalid

**Expected Result:**
- Validasi mencegah submit
- Error message muncul

### 5. **Pertumbuhan Balita**

#### Test Case 5.1: Tampilan Grafik
**Prerequisites:** Balita memiliki riwayat pertumbuhan
**Steps:**
1. Buka tab "Pertumbuhan"
2. Cari balita dengan riwayat
3. Klik untuk melihat grafik

**Expected Result:**
- Grafik KMS ditampilkan
- Garis berat dan tinggi terlihat
- Legend dan grid lines ada
- Scaling otomatis

#### Test Case 5.2: Grafik Kosong
**Steps:**
1. Cari balita tanpa riwayat
2. Klik untuk melihat grafik

**Expected Result:**
- Pesan "Belum ada data pertumbuhan"
- Tidak ada grafik

### 6. **Ekspor CSV**

#### Test Case 6.1: Ekspor Data
**Prerequisites:** Ada data balita
**Steps:**
1. Buka tab "Ekspor"
2. Klik "Ekspor ke CSV"
3. Verifikasi file terbuat

**Expected Result:**
- File CSV terbuat
- Pesan sukses muncul
- Tombol "Bagikan File" muncul

#### Test Case 6.2: Share File
**Steps:**
1. Setelah ekspor berhasil
2. Klik "Bagikan File"

**Expected Result:**
- Intent share muncul
- File bisa dibagikan ke aplikasi lain

#### Test Case 6.3: Ekspor Kosong
**Prerequisites:** Tidak ada data balita
**Steps:**
1. Buka tab "Ekspor"
2. Coba ekspor

**Expected Result:**
- Button disabled
- Pesan "Tidak ada data untuk diekspor"

## 🔧 Test Environment

### Device Testing
- **Android 8.0+ (API 27+)**
- **Screen sizes:** Phone, Tablet
- **Orientations:** Portrait, Landscape

### Network Testing
- **WiFi:** Test dengan koneksi stabil
- **Mobile Data:** Test dengan 3G/4G
- **Offline:** Test tanpa internet

### Firebase Testing
- **Development:** Test dengan Firebase test mode
- **Production:** Test dengan Firebase production rules

## 🐛 Bug Reporting

### Format Bug Report
```
**Bug Title:** [Deskripsi singkat]

**Environment:**
- Device: [Model Android]
- OS Version: [Android version]
- App Version: [Version aplikasi]

**Steps to Reproduce:**
1. [Step 1]
2. [Step 2]
3. [Step 3]

**Expected Result:**
[Hasil yang diharapkan]

**Actual Result:**
[Hasil yang terjadi]

**Screenshots:**
[Gambar jika ada]

**Additional Info:**
[Informasi tambahan]
```

## 📊 Test Metrics

### Performance Testing
- **App Launch Time:** < 3 detik
- **Screen Navigation:** < 1 detik
- **Data Loading:** < 2 detik
- **Memory Usage:** < 100MB

### Usability Testing
- **Task Completion Rate:** > 95%
- **Error Rate:** < 5%
- **User Satisfaction:** > 4.0/5.0

## 🚀 Automation Testing

### Unit Tests
```kotlin
@Test
fun testAddBalita() {
    // Test adding balita
}

@Test
fun testSearchBalita() {
    // Test search functionality
}
```

### UI Tests
```kotlin
@Test
fun testInputForm() {
    // Test input form UI
}

@Test
fun testNavigation() {
    // Test navigation between screens
}
```

## 📱 Manual Testing Checklist

### Pre-release Checklist
- [ ] Semua fitur utama berfungsi
- [ ] UI/UX sesuai design
- [ ] Performance acceptable
- [ ] Error handling proper
- [ ] Data validation working
- [ ] Firebase integration stable
- [ ] File export working
- [ ] Search functionality working
- [ ] Navigation smooth
- [ ] Responsive design

### Post-release Checklist
- [ ] Monitor crash reports
- [ ] Check user feedback
- [ ] Monitor performance metrics
- [ ] Verify data integrity
- [ ] Test edge cases 