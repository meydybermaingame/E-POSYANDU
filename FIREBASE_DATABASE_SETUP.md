# Firebase Realtime Database Setup Guide

## ⚠️ PENTING: Database Belum Dibuat

Berdasarkan error log yang muncul:
```
Firebase Database connection was forcefully killed by the server. 
Will not attempt reconnect. Reason: Firebase error. 
Please ensure that you have the URL of your Firebase Realtime Database instance configured correctly.
```

Ini menunjukkan bahwa **Firebase Realtime Database belum dibuat** di Firebase Console.

## 🚀 Langkah-langkah Setup Database

### 1. Buka Firebase Console
1. Pergi ke [Firebase Console](https://console.firebase.google.com/)
2. Pilih project `e-posyandu-app`

### 2. Buat Realtime Database
1. Di sidebar kiri, klik **"Realtime Database"**
2. Klik **"Create Database"**
3. Pilih lokasi database: **"asia-southeast1"** (Singapore)
4. Pilih **"Start in test mode"** untuk development
5. Klik **"Done"**

### 3. Atur Database Rules
Setelah database dibuat, atur rules untuk mengizinkan read/write:

1. Di tab **"Rules"**, ganti dengan:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

2. Klik **"Publish"**

### 4. Verifikasi URL Database
Setelah database dibuat, URL akan muncul di bagian atas halaman:
```
https://e-posyandu-app-default-rtdb.asia-southeast1.firebasedatabase.app
```

### 5. Test Database
Setelah setup selesai:
1. Jalankan aplikasi Android
2. Tekan tombol "Data Demo" 
3. Cek log untuk memastikan tidak ada error

## 🔧 Rules untuk Production

Untuk production, gunakan rules yang lebih aman:
```json
{
  "rules": {
    "balita": {
      ".read": true,
      ".write": true,
      "$balitaId": {
        ".validate": "newData.hasChildren(['nama', 'usia', 'berat', 'tinggi'])"
      }
    },
    "sensor": {
      ".read": true,
      ".write": true
    },
    "riwayat": {
      ".read": true,
      ".write": true
    }
  }
}
```

## 🚨 Troubleshooting

### Jika masih error setelah setup:
1. **Refresh aplikasi** - tutup dan buka kembali
2. **Clean & Rebuild** project Android
3. **Cek internet connection**
4. **Verifikasi URL** di Firebase Console

### Cek apakah database sudah dibuat:
1. Buka Firebase Console
2. Pilih project `e-posyandu-app`
3. Klik "Realtime Database" di sidebar
4. Jika muncul "Create Database", berarti belum dibuat
5. Jika muncul data/rules, berarti sudah dibuat

## ✅ Setelah Setup Berhasil

Setelah database dibuat dan rules diatur:
1. Aplikasi akan bisa connect ke Firebase
2. Data dummy bisa ditambahkan
3. Data akan muncul di UI aplikasi
4. Log tidak akan menampilkan error connection

---

**CATATAN**: Error "Firebase Database connection was forcefully killed" hampir selalu disebabkan oleh database yang belum dibuat di Firebase Console. Setup database adalah langkah wajib yang harus dilakukan sebelum aplikasi bisa berfungsi.