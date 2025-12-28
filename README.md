# Aplikasi Posyandu

Aplikasi Android untuk manajemen data balita di Posyandu dengan integrasi IoT (ESP32) untuk pengukuran otomatis berat dan tinggi badan.

## 🚀 Fitur Utama

### 1. **Input Data Balita**
- Form pendaftaran balita lengkap
- Integrasi dengan sensor IoT (Load Cell & VL53L0X)
- Tombol untuk mengambil data berat dan tinggi otomatis dari Firebase
- Validasi input data

### 2. **Data Balita**
- Daftar semua balita terdaftar
- Fitur pencarian berdasarkan nama
- Aksi: Lihat detail, Edit, Hapus
- Tampilan card yang informatif

### 3. **Pertumbuhan Balita**
- Grafik pertumbuhan KMS (Kartu Menuju Sehat)
- Visualisasi data berat dan tinggi badan
- Pencarian balita untuk melihat grafik
- Riwayat pertumbuhan lengkap

### 4. **Edit Data Balita**
- Form edit data balita
- Nama balita tidak dapat diubah (sebagai ID)
- Integrasi dengan sensor IoT untuk update data

### 5. **Ekspor CSV**
- Export semua data balita ke file CSV
- Termasuk riwayat pertumbuhan
- Fitur share file
- Penyimpanan di storage internal aplikasi

## 🛠️ Teknologi yang Digunakan

- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - UI framework modern
- **Firebase Realtime Database** - Database cloud
- **Navigation Compose** - Navigasi antar screen
- **ViewModel & StateFlow** - State management
- **Coroutines** - Asynchronous programming
- **OpenCSV** - Library untuk ekspor CSV

## 🔧 Setup Project

### Prerequisites
- Android Studio Arctic Fox atau lebih baru
- Android SDK 27+
- Firebase project

### Installation

1. **Clone repository**
```bash
git clone <repository-url>
cd E-POSYANDU
```

2. **Setup Firebase**
   - Buat project di [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json`
   - Letakkan file di folder `app/`
   - Aktifkan Realtime Database di Firebase Console

3. **Build dan Run**
```bash
./gradlew build
```

### Struktur Database Firebase

```json
{
  "sensor": {
    "berat": 10.2,
    "tinggi": 80.5
  },
  "balita": {
    "nama_balita_1": {
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
  }
}
```

## 📱 Integrasi IoT (ESP32)

### Hardware Requirements
- ESP32 Development Board
- Load Cell Sensor (untuk berat badan)
- VL53L0X Sensor (untuk tinggi badan)
- Kabel jumper

### ESP32 Code Structure
```cpp
// Contoh struktur data yang dikirim ke Firebase
{
  "sensor": {
    "berat": 10.2,    // dari Load Cell
    "tinggi": 80.5    // dari VL53L0X
  }
}
```

### Setup ESP32
1. Install library Firebase ESP32
2. Konfigurasi WiFi dan Firebase credentials
3. Setup sensor Load Cell dan VL53L0X
4. Upload kode ke ESP32

## 🎨 UI/UX Features

- **Material Design 3** - Design system modern
- **Dark/Light Theme** - Support tema sistem
- **Responsive Design** - Adaptif berbagai ukuran layar
- **Accessibility** - Support accessibility features
- **Smooth Animations** - Transisi yang halus

## 📊 Fitur Chart KMS

- Grafik garis untuk berat dan tinggi badan
- Grid lines untuk memudahkan pembacaan
- Legend untuk membedakan data
- Scaling otomatis berdasarkan data
- Support multiple data points

## 🔐 Security & Permissions

### Permissions Required
- `INTERNET` - Koneksi ke Firebase
- `ACCESS_NETWORK_STATE` - Status jaringan
- `WRITE_EXTERNAL_STORAGE` - Ekspor CSV (Android < 29)
- `READ_EXTERNAL_STORAGE` - Baca file (Android < 33)

### Security Features
- Firebase Authentication (opsional)
- Data validation
- Secure file sharing dengan FileProvider

## 🚀 Deployment

### Build APK
```bash
./gradlew assembleRelease
```

### Build AAB (Google Play Store)
```bash
./gradlew bundleRelease
```

## 📝 Changelog

### Version 1.0.0
- ✅ Input data balita dengan integrasi IoT
- ✅ Daftar dan manajemen data balita
- ✅ Grafik pertumbuhan KMS
- ✅ Edit data balita
- ✅ Ekspor data ke CSV
- ✅ Integrasi Firebase Realtime Database

## 🤝 Contributing

1. Fork repository
2. Buat feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

## 📞 Support

Untuk dukungan teknis atau pertanyaan:
- Email: support@posyandu-app.com
- Issues: [GitHub Issues](https://github.com/username/E-POSYANDU/issues)

## 🙏 Acknowledgments

- Firebase untuk backend services
- Jetpack Compose team untuk UI framework
- OpenCSV untuk library CSV
- Material Design team untuk design system 