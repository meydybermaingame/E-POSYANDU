# Troubleshooting Aplikasi Posyandu

Panduan lengkap untuk mengatasi masalah yang sering terjadi pada aplikasi Posyandu.

## 🔧 Common Issues

### 1. **Firebase Connection Issues**

#### Problem: "Failed to get document" atau "Network error"
**Symptoms:**
- Data tidak muncul
- Error message "Network error"
- Loading spinner tidak berhenti

**Solutions:**
1. **Check Internet Connection**
   ```bash
   # Test internet connection
   ping google.com
   ```

2. **Verify Firebase Configuration**
   - Pastikan `google-services.json` ada di folder `app/`
   - Cek package name di Firebase Console
   - Verifikasi API key

3. **Check Firebase Rules**
   ```json
   // Test mode untuk development
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```

4. **Clear App Data**
   - Settings > Apps > Aplikasi Posyandu > Storage > Clear Data

#### Problem: "Permission denied"
**Solutions:**
1. **Update Firebase Rules**
   ```json
   {
     "rules": {
       "sensor": {
         ".read": true,
         ".write": true
       },
       "balita": {
         ".read": true,
         ".write": true
       }
     }
   }
   ```

2. **Check Authentication** (jika menggunakan auth)
   - Pastikan user sudah login
   - Cek token authentication

### 2. **Data Input Issues**

#### Problem: Form tidak bisa disubmit
**Symptoms:**
- Button "Daftar" disabled
- Error validation muncul

**Solutions:**
1. **Check Required Fields**
   - Nama Balita (wajib)
   - Usia (wajib, > 0)
   - Berat Badan (wajib, > 0)
   - Tinggi Badan (wajib, > 0)

2. **Validate Input Format**
   ```kotlin
   // Contoh validasi
   if (namaBalita.isEmpty()) {
       // Show error
   }
   if (usia.toIntOrNull() == null || usia.toInt() <= 0) {
       // Show error
   }
   ```

#### Problem: Data tidak tersimpan
**Solutions:**
1. **Check Firebase Connection**
2. **Verify Data Structure**
3. **Check Console Logs**
4. **Test with Simple Data**

### 3. **Sensor IoT Issues**

#### Problem: Data sensor tidak muncul
**Symptoms:**
- Card "Data Sensor IoT" tidak muncul
- Button "Ambil Berat/Tinggi" disabled

**Solutions:**
1. **Check ESP32 Connection**
   ```cpp
   // Test ESP32 connection
   Serial.println("Testing connection...");
   ```

2. **Verify Firebase Data Structure**
   ```json
   {
     "sensor": {
       "berat": 10.2,
       "tinggi": 80.5
     }
   }
   ```

3. **Check ESP32 Code**
   - WiFi credentials
   - Firebase credentials
   - Sensor calibration

#### Problem: Data sensor tidak akurat
**Solutions:**
1. **Calibrate Load Cell**
   ```cpp
   // Kalibrasi dengan berat yang diketahui
   float calibration_factor = -7050.0;
   ```

2. **Calibrate VL53L0X**
   ```cpp
   // Kalibrasi offset
   float height_offset = 10.0;
   ```

### 4. **UI/UX Issues**

#### Problem: App crash saat buka
**Symptoms:**
- App force close
- Black screen
- Loading tidak selesai

**Solutions:**
1. **Check Logs**
   ```bash
   adb logcat | grep "com.example.e_posyandu"
   ```

2. **Clear App Data**
3. **Reinstall App**
4. **Check Device Compatibility**

#### Problem: Navigation tidak berfungsi
**Solutions:**
1. **Check Navigation Setup**
   ```kotlin
   // Verify navigation routes
   composable("data_balita") { ... }
   ```

2. **Check Dependencies**
   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.7.6")
   ```

### 5. **CSV Export Issues**

#### Problem: File CSV tidak terbuat
**Symptoms:**
- Error "Gagal membuat file CSV"
- File tidak ada di storage

**Solutions:**
1. **Check Permissions**
   ```xml
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   ```

2. **Check File Provider**
   ```xml
   <provider
       android:name="androidx.core.content.FileProvider"
       android:authorities="${applicationId}.fileprovider"
       android:exported="false"
       android:grantUriPermissions="true">
   ```

3. **Check Storage Space**
4. **Verify OpenCSV Library**

#### Problem: File tidak bisa dibagikan
**Solutions:**
1. **Check File Provider Paths**
   ```xml
   <paths xmlns:android="http://schemas.android.com/apk/res/android">
       <external-files-path name="my_files" path="." />
   </paths>
   ```

2. **Check Intent Configuration**
   ```kotlin
   val shareIntent = Intent().apply {
       action = Intent.ACTION_SEND
       putExtra(Intent.EXTRA_STREAM, uri)
       type = "text/csv"
   }
   ```

## 🐛 Debugging Tools

### 1. **Android Studio Debugger**

#### Setup Debugging
1. Set breakpoints di kode
2. Run app in debug mode
3. Monitor variables dan call stack

#### Common Debug Points
```kotlin
// Firebase operations
repository.addBalita(balita)

// UI state changes
val balitaList by viewModel.balitaList.collectAsState()

// Navigation
navController.navigate("input_balita")
```

### 2. **Firebase Console**

#### Monitor Database
1. Buka Firebase Console
2. Realtime Database
3. Monitor data changes real-time

#### Check Rules
1. Test rules di Firebase Console
2. Verify read/write permissions

### 3. **Logcat**

#### Filter Logs
```bash
# Filter by package
adb logcat | grep "com.example.e_posyandu"

# Filter by tag
adb logcat | grep "Firebase"

# Filter by level
adb logcat *:E
```

#### Add Custom Logs
```kotlin
Log.d("Posyandu", "Adding balita: ${balita.nama}")
Log.e("Posyandu", "Error: ${exception.message}")
```

## 🔍 Performance Issues

### 1. **Slow App Loading**

#### Solutions:
1. **Optimize Dependencies**
   ```kotlin
   // Remove unused dependencies
   // Use specific versions
   ```

2. **Lazy Loading**
   ```kotlin
   LazyColumn {
       items(balitaList) { balita ->
           BalitaItem(balita = balita)
       }
   }
   ```

3. **Image Optimization**
   - Use vector drawables
   - Compress images
   - Implement caching

### 2. **High Memory Usage**

#### Solutions:
1. **Memory Leaks**
   ```kotlin
   // Proper lifecycle management
   DisposableEffect(Unit) {
       onDispose {
           // Cleanup resources
       }
   }
   ```

2. **Large Lists**
   ```kotlin
   // Pagination
   LazyColumn {
       items(balitaList.take(20)) { balita ->
           BalitaItem(balita = balita)
       }
   }
   ```

### 3. **Battery Drain**

#### Solutions:
1. **Network Optimization**
   - Reduce API calls
   - Implement caching
   - Use efficient data structures

2. **Background Processing**
   - Minimize background tasks
   - Use WorkManager for heavy tasks

## 🚨 Emergency Procedures

### 1. **App Crashes Frequently**

#### Immediate Actions:
1. **Clear App Data**
2. **Restart Device**
3. **Check Available Storage**
4. **Update App**

#### Investigation:
1. **Check Crash Reports**
2. **Review Recent Changes**
3. **Test on Different Devices**

### 2. **Data Loss**

#### Recovery Steps:
1. **Check Firebase Backup**
   ```bash
   firebase database:get / > backup.json
   ```

2. **Restore from Backup**
   ```bash
   firebase database:set / backup.json
   ```

3. **Verify Data Integrity**

### 3. **Security Breach**

#### Immediate Actions:
1. **Disable App Access**
2. **Review Firebase Rules**
3. **Check Authentication Logs**
4. **Update Security Rules**

## 📞 Support Channels

### 1. **Developer Support**
- Email: developer@posyandu-app.com
- GitHub Issues: [Repository Issues](https://github.com/username/E-POSYANDU/issues)
- Documentation: [Project Wiki](https://github.com/username/E-POSYANDU/wiki)

### 2. **User Support**
- In-app Feedback
- Email Support
- FAQ Section
- Video Tutorials

### 3. **Community Support**
- Stack Overflow
- Reddit r/androiddev
- Firebase Community

## 🔧 Maintenance Tips

### 1. **Regular Maintenance**
- Weekly: Check crash reports
- Monthly: Performance review
- Quarterly: Security audit
- Annually: Major updates

### 2. **Monitoring**
- Firebase Analytics
- Crashlytics
- Performance Monitoring
- User Feedback

### 3. **Updates**
- Keep dependencies updated
- Monitor security patches
- Test new Android versions
- Update target SDK

## 📋 Troubleshooting Checklist

### Before Reporting Issues
- [ ] Check internet connection
- [ ] Restart app
- [ ] Clear app data
- [ ] Update app
- [ ] Check device storage
- [ ] Verify Firebase configuration
- [ ] Test on different device
- [ ] Check logs for errors

### When Reporting Issues
- [ ] Device model and OS version
- [ ] App version
- [ ] Steps to reproduce
- [ ] Expected vs actual behavior
- [ ] Screenshots/videos
- [ ] Log files
- [ ] Error messages 