# Build Script for Aplikasi Posyandu (PowerShell)
# This script automates the build process for different build types

param(
    [Parameter(Position=0)]
    [string]$Action = "help"
)

# Colors for output
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"
$White = "White"

# Function to print colored output
function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Red
}

# Function to check if required tools are installed
function Test-Requirements {
    Write-Status "Checking requirements..."
    
    # Check if Java is installed
    try {
        $javaVersion = java -version 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Java not found"
        }
    }
    catch {
        Write-Error "Java is not installed. Please install Java 11 or higher."
        exit 1
    }
    
    # Check if Gradle wrapper exists
    if (-not (Test-Path "gradlew.bat")) {
        Write-Error "Gradle wrapper not found. Please run this script from the project root."
        exit 1
    }
    
    # Check if google-services.json exists
    if (-not (Test-Path "app\google-services.json")) {
        Write-Warning "google-services.json not found. Please add it to the app\ directory."
    }
    
    Write-Success "Requirements check completed."
}

# Function to clean project
function Invoke-CleanProject {
    Write-Status "Cleaning project..."
    & .\gradlew.bat clean
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to clean project."
        exit 1
    }
    Write-Success "Project cleaned successfully."
}

# Function to run tests
function Invoke-RunTests {
    Write-Status "Running tests..."
    & .\gradlew.bat test
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Tests failed."
        exit 1
    }
    Write-Success "Tests completed successfully."
}

# Function to run instrumented tests
function Invoke-RunInstrumentedTests {
    Write-Status "Running instrumented tests..."
    & .\gradlew.bat connectedAndroidTest
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Instrumented tests failed."
        exit 1
    }
    Write-Success "Instrumented tests completed successfully."
}

# Function to build debug APK
function Invoke-BuildDebug {
    Write-Status "Building debug APK..."
    & .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to build debug APK."
        exit 1
    }
    Write-Success "Debug APK built successfully."
    Write-Status "APK location: app\build\outputs\apk\debug\app-debug.apk"
}

# Function to build release APK
function Invoke-BuildRelease {
    Write-Status "Building release APK..."
    
    # Check if keystore exists
    if (-not (Test-Path "app\keystore\release.keystore")) {
        Write-Warning "Release keystore not found. Building unsigned release APK."
        & .\gradlew.bat assembleRelease
    } else {
        Write-Status "Using release keystore for signing."
        & .\gradlew.bat assembleRelease
    }
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to build release APK."
        exit 1
    }
    Write-Success "Release APK built successfully."
    Write-Status "APK location: app\build\outputs\apk\release\app-release.apk"
}

# Function to build release AAB
function Invoke-BuildAAB {
    Write-Status "Building release AAB..."
    
    # Check if keystore exists
    if (-not (Test-Path "app\keystore\release.keystore")) {
        Write-Warning "Release keystore not found. Building unsigned release AAB."
        & .\gradlew.bat bundleRelease
    } else {
        Write-Status "Using release keystore for signing."
        & .\gradlew.bat bundleRelease
    }
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to build release AAB."
        exit 1
    }
    Write-Success "Release AAB built successfully."
    Write-Status "AAB location: app\build\outputs\bundle\release\app-release.aab"
}

# Function to install debug APK on connected device
function Invoke-InstallDebug {
    Write-Status "Installing debug APK on connected device..."
    
    # Check if device is connected
    $devices = adb devices | Select-String "device$"
    if (-not $devices) {
        Write-Error "No Android device connected. Please connect a device and enable USB debugging."
        exit 1
    }
    
    & .\gradlew.bat installDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to install debug APK."
        exit 1
    }
    Write-Success "Debug APK installed successfully."
}

# Function to run lint
function Invoke-RunLint {
    Write-Status "Running lint checks..."
    & .\gradlew.bat lint
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "Lint found issues. Check the report for details."
    } else {
        Write-Success "Lint checks completed."
    }
}

# Function to generate build report
function Invoke-GenerateReport {
    Write-Status "Generating build report..."
    
    # Create reports directory
    if (-not (Test-Path "reports")) {
        New-Item -ItemType Directory -Path "reports" | Out-Null
    }
    
    # Generate dependency report
    & .\gradlew.bat dependencies | Out-File -FilePath "reports\dependencies.txt" -Encoding UTF8
    
    Write-Success "Build report generated in reports\ directory."
}

# Function to show help
function Show-Help {
    Write-Host "Aplikasi Posyandu Build Script" -ForegroundColor $White
    Write-Host ""
    Write-Host "Usage: .\build.ps1 [OPTION]" -ForegroundColor $White
    Write-Host ""
    Write-Host "Options:" -ForegroundColor $White
    Write-Host "  clean           Clean the project" -ForegroundColor $White
    Write-Host "  test            Run unit tests" -ForegroundColor $White
    Write-Host "  instrumented    Run instrumented tests" -ForegroundColor $White
    Write-Host "  debug           Build debug APK" -ForegroundColor $White
    Write-Host "  release         Build release APK" -ForegroundColor $White
    Write-Host "  aab             Build release AAB" -ForegroundColor $White
    Write-Host "  install         Install debug APK on connected device" -ForegroundColor $White
    Write-Host "  lint            Run lint checks" -ForegroundColor $White
    Write-Host "  report          Generate build report" -ForegroundColor $White
    Write-Host "  all             Run all checks and build debug APK" -ForegroundColor $White
    Write-Host "  help            Show this help message" -ForegroundColor $White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor $White
    Write-Host "  .\build.ps1 debug        # Build debug APK" -ForegroundColor $White
    Write-Host "  .\build.ps1 release      # Build release APK" -ForegroundColor $White
    Write-Host "  .\build.ps1 all          # Run all checks and build" -ForegroundColor $White
}

# Function to run all tasks
function Invoke-AllTasks {
    Test-Requirements
    Invoke-CleanProject
    Invoke-RunTests
    Invoke-RunLint
    Invoke-BuildDebug
    Invoke-GenerateReport
    Write-Success "All tasks completed successfully!"
}

# Main script logic
switch ($Action.ToLower()) {
    "clean" { Invoke-CleanProject }
    "test" { Invoke-RunTests }
    "instrumented" { Invoke-RunInstrumentedTests }
    "debug" { Invoke-BuildDebug }
    "release" { Invoke-BuildRelease }
    "aab" { Invoke-BuildAAB }
    "install" { Invoke-InstallDebug }
    "lint" { Invoke-RunLint }
    "report" { Invoke-GenerateReport }
    "all" { Invoke-AllTasks }
    "help" { Show-Help }
    default {
        Write-Error "Unknown option: $Action"
        Show-Help
    }
} 