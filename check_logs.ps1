# Script untuk melihat log Android
Write-Host "Mencari adb.exe..."

$adbPaths = @(
    "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
    "$env:USERPROFILE\AppData\Local\Android\Sdk\platform-tools\adb.exe",
    "C:\Android\Sdk\platform-tools\adb.exe"
)

$adbFound = $false
foreach ($path in $adbPaths) {
    if (Test-Path $path) {
        Write-Host "Ditemukan adb di: $path"
        Write-Host "Memulai monitoring log Android..."
        Write-Host "Tekan Ctrl+C untuk berhenti"
        Write-Host "Filter: DummyDataGenerator, HomeScreen, Firebase"
        Write-Host ""
        
        # Filter log untuk DummyDataGenerator, HomeScreen, dan Firebase
        & $path logcat -v time | Select-String "DummyDataGenerator|HomeScreen|Firebase|E-POSYANDU"
        $adbFound = $true
        break
    }
}

if (-not $adbFound) {
    Write-Host "ADB tidak ditemukan di lokasi standar"
    Write-Host "Pastikan Android SDK sudah terinstall"
}