# Setup Firebase untuk Aplikasi Posyandu

Panduan lengkap untuk mengkonfigurasi Firebase sebagai backend untuk aplikasi Posyandu.

## 📋 Prerequisites

- Akun Google
- Android Studio
- Project Android yang sudah dibuat

## 🚀 Langkah-langkah Setup

### 1. Membuat Project Firebase

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Klik "Create a project" atau "Add project"
3. Masukkan nama project: `e-posyandu-app`
4. Pilih apakah ingin mengaktifkan Google Analytics (opsional)
5. Klik "Create project"

### 2. Menambahkan Android App

1. Di Firebase Console, klik ikon Android
2. Masukkan package name: `com.example.e_posyandu`
3. Masukkan app nickname: `Aplikasi Posyandu`
4. Klik "Register app"

### 3. Download google-services.json

1. Download file `google-services.json`
2. Letakkan file di folder `app/` di project Android
3. Klik "Next" dan "Continue to console"

### 4. Mengaktifkan Realtime Database

1. Di sidebar Firebase Console, klik "Realtime Database"
2. Klik "Create Database"
3. Pilih lokasi database (pilih yang terdekat)
4. Pilih mode: "Start in test mode" (untuk development)
5. Klik "Done"

### 5. Konfigurasi Database Rules

1. Di Realtime Database, klik tab "Rules"
2. Ganti rules dengan yang berikut:

```json
{
  "rules": {
    "sensor": {
      ".read": true,
      ".write": true
    },
    "balita": {
      ".read": true,
      ".write": true,
      "$nama": {
        ".validate": "newData.hasChildren(['nama', 'usia', 'berat', 'tinggi'])"
      }
    }
  }
}
```

### 6. Testing Koneksi

1. Jalankan aplikasi Android
2. Cek di Firebase Console apakah data muncul
3. Test input data balita
4. Verifikasi data tersimpan dengan benar

## 🔧 Konfigurasi Tambahan

### Authentication (Opsional)

Jika ingin menambahkan login:

1. Di Firebase Console, klik "Authentication"
2. Klik "Get started"
3. Pilih "Email/Password" sebagai sign-in method
4. Aktifkan dan simpan

### Storage (Opsional)

Untuk menyimpan file CSV:

1. Di Firebase Console, klik "Storage"
2. Klik "Get started"
3. Pilih lokasi storage
4. Set rules sesuai kebutuhan

## 📊 Struktur Database

### Node: `/sensor`
```json
{
  "berat": 10.2,
  "tinggi": 80.5
}
```

### Node: `/balita/{nama_balita}`
```json
{
  "nama": "Nama Balita",
  "namaAyah": "Nama Ayah",
  "namaIbu": "Nama Ibu",
  "usia": 2,
  "lingkarKepala": 45.0,
  "keterangan": "Keterangan tambahan",
  "berat": 10.2,
  "tinggi": 80.5,
  "riwayat": [
    {
      "tanggal": "2024-06-01",
      "berat": 10.2,
      "tinggi": 80.5
    }
  ],
  "tanggalDaftar": "2024-06-01 10:30:00"
}
```

## 🔐 Security Rules

### Rules untuk Production

```json
{
  "rules": {
    "sensor": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "balita": {
      ".read": "auth != null",
      ".write": "auth != null",
      "$nama": {
        ".validate": "newData.hasChildren(['nama', 'usia', 'berat', 'tinggi']) && 
                      newData.child('nama').isString() &&
                      newData.child('usia').isNumber() &&
                      newData.child('berat').isNumber() &&
                      newData.child('tinggi').isNumber()"
      }
    }
  }
}
```

## 🚨 Troubleshooting

### Error: "Failed to get document"
- Pastikan internet connection aktif
- Cek apakah `google-services.json` sudah benar
- Verifikasi package name di Firebase Console

### Error: "Permission denied"
- Cek database rules
- Pastikan mode database sudah benar
- Verifikasi path data yang diakses

### Data tidak muncul
- Cek koneksi internet
- Verifikasi struktur data yang dikirim
- Cek console log untuk error

## 📱 Testing dengan ESP32

1. Update kode ESP32 dengan credentials Firebase yang benar
2. Pastikan ESP32 terhubung ke WiFi
3. Test pengiriman data sensor
4. Verifikasi data muncul di Firebase Console

## 🔄 Backup & Restore

### Export Data
```bash
# Menggunakan Firebase CLI
firebase database:get / > backup.json
```

### Import Data
```bash
# Menggunakan Firebase CLI
firebase database:set / backup.json
```

## 📞 Support

Jika mengalami masalah:
1. Cek [Firebase Documentation](https://firebase.google.com/docs)
2. Lihat [Firebase Community](https://firebase.google.com/community)
3. Buat issue di GitHub repository

## 🔗 Links Penting

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Realtime Database Rules](https://firebase.google.com/docs/database/security) 