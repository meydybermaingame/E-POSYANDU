# Panduan Deployment Aplikasi Posyandu

Panduan lengkap untuk deployment aplikasi Posyandu ke production.

## 🚀 Pre-deployment Checklist

### Code Review
- [ ] Semua fitur utama berfungsi
- [ ] Unit tests passed
- [ ] UI tests passed
- [ ] Performance acceptable
- [ ] Security review completed
- [ ] Code documentation updated

### Firebase Setup
- [ ] Production Firebase project created
- [ ] Database rules configured for production
- [ ] Authentication enabled (if needed)
- [ ] Storage configured (if needed)
- [ ] Backup strategy implemented

### Android Configuration
- [ ] `google-services.json` updated for production
- [ ] App signing configured
- [ ] ProGuard rules configured
- [ ] Version code and name updated
- [ ] Permissions reviewed

## 📱 Build Configuration

### 1. Update Version

**File:** `app/build.gradle.kts`
```kotlin
android {
    defaultConfig {
        versionCode = 2  // Increment this
        versionName = "1.1.0"  // Update version name
    }
}
```

### 2. Configure App Signing

**File:** `app/build.gradle.kts`
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("keystore/release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 3. ProGuard Configuration

**File:** `app/proguard-rules.pro`
```proguard
# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# OpenCSV
-keep class com.opencsv.** { *; }

# Keep data models
-keep class com.example.e_posyandu.data.model.** { *; }
```

## 🔐 Security Configuration

### 1. Firebase Security Rules

**Production Rules:**
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
                      newData.child('tinggi').isNumber() &&
                      newData.child('usia').val() > 0 &&
                      newData.child('berat').val() > 0 &&
                      newData.child('tinggi').val() > 0"
      }
    }
  }
}
```

### 2. Network Security

**File:** `app/src/main/res/xml/network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">firebaseio.com</domain>
        <domain includeSubdomains="true">googleapis.com</domain>
    </domain-config>
</network-security-config>
```

### 3. AndroidManifest.xml Updates

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="false">
    <!-- ... -->
</application>
```

## 🏗️ Build Process

### 1. Generate Keystore

```bash
keytool -genkey -v -keystore app/keystore/release.keystore \
    -alias posyandu-key \
    -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Set Environment Variables

```bash
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="posyandu-key"
export KEY_PASSWORD="your_key_password"
```

### 3. Build Release APK

```bash
./gradlew assembleRelease
```

### 4. Build Release AAB (Google Play Store)

```bash
./gradlew bundleRelease
```

## 📦 Distribution Options

### 1. Google Play Store

#### Prerequisites
- Google Play Console account
- App signing enabled
- Privacy policy
- Content rating

#### Upload Process
1. Build AAB file
2. Upload to Google Play Console
3. Fill app information:
   - App description
   - Screenshots
   - Feature graphic
   - Privacy policy
4. Set content rating
5. Submit for review

### 2. Internal Testing

#### Firebase App Distribution
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Upload APK
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
    --app FIREBASE_APP_ID \
    --release-notes "Version 1.1.0 - Bug fixes and improvements" \
    --groups "testers"
```

### 3. Direct APK Distribution

#### Generate APK
```bash
./gradlew assembleRelease
```

#### APK Location
```
app/build/outputs/apk/release/app-release.apk
```

## 🔄 CI/CD Pipeline

### GitHub Actions

**File:** `.github/workflows/deploy.yml`
```yaml
name: Deploy to Firebase App Distribution

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build with Gradle
      run: ./gradlew assembleRelease
      env:
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
    
    - name: Upload APK to Firebase App Distribution
      uses: wzieba/Firebase-Distribution-Github-Action@v1
      with:
        appId: ${{ secrets.FIREBASE_APP_ID }}
        serviceCredentialsFileContent: ${{ secrets.FIREBASE_SERVICE_ACCOUNT_KEY }}
        groups: testers
        file: app/build/outputs/apk/release/app-release.apk
        releaseNotes: ${{ github.event.head_commit.message }}
```

## 📊 Monitoring & Analytics

### 1. Firebase Analytics

**Add to MainActivity:**
```kotlin
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics
        // ...
    }
}
```

### 2. Crash Reporting

**Add to build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.0")
}
```

### 3. Performance Monitoring

**Add to build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.google.firebase:firebase-perf-ktx:1.4.2")
}
```

## 🔍 Post-deployment Checklist

### Immediate (Day 1)
- [ ] Monitor crash reports
- [ ] Check user feedback
- [ ] Verify data integrity
- [ ] Monitor performance metrics
- [ ] Test critical user flows

### Short-term (Week 1)
- [ ] Analyze user engagement
- [ ] Review error logs
- [ ] Gather user feedback
- [ ] Plan bug fixes
- [ ] Monitor Firebase usage

### Long-term (Month 1)
- [ ] Performance optimization
- [ ] Feature requests analysis
- [ ] Security audit
- [ ] Backup verification
- [ ] Update planning

## 🚨 Rollback Plan

### Emergency Rollback
1. **Immediate Actions:**
   - Disable new version in Play Store
   - Revert to previous version
   - Notify users if necessary

2. **Investigation:**
   - Analyze crash reports
   - Identify root cause
   - Fix critical issues

3. **Hotfix Release:**
   - Build and test hotfix
   - Deploy emergency update
   - Monitor closely

### Data Recovery
1. **Firebase Backup:**
   ```bash
   firebase database:get / > backup_$(date +%Y%m%d_%H%M%S).json
   ```

2. **Restore Process:**
   ```bash
   firebase database:set / backup_file.json
   ```

## 📈 Performance Optimization

### 1. APK Size Optimization
- Enable R8/ProGuard
- Remove unused resources
- Use vector drawables
- Enable resource shrinking

### 2. Runtime Performance
- Lazy loading for lists
- Image caching
- Network request optimization
- Memory leak prevention

### 3. Battery Optimization
- Efficient background processing
- Optimized sensor usage
- Minimal network calls
- Proper lifecycle management

## 🔐 Security Best Practices

### 1. Code Obfuscation
- Enable ProGuard/R8
- Obfuscate sensitive strings
- Remove debug information

### 2. Network Security
- Use HTTPS only
- Certificate pinning (if needed)
- Secure API communication

### 3. Data Protection
- Encrypt sensitive data
- Secure file storage
- Proper permission handling

## 📞 Support & Maintenance

### 1. User Support
- In-app feedback system
- Email support
- FAQ documentation
- Video tutorials

### 2. Maintenance Schedule
- Weekly: Monitor crash reports
- Monthly: Performance review
- Quarterly: Security audit
- Annually: Major version update

### 3. Update Strategy
- Regular bug fixes
- Feature updates
- Security patches
- Performance improvements 