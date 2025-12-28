@echo off
REM Build Script for Aplikasi Posyandu (Windows)
REM This script automates the build process for different build types

setlocal enabledelayedexpansion

REM Colors for output (Windows 10+)
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Function to print colored output
:print_status
echo %BLUE%[INFO]%NC% %~1
goto :eof

:print_success
echo %GREEN%[SUCCESS]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

REM Function to check if required tools are installed
:check_requirements
call :print_status "Checking requirements..."

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    call :print_error "Java is not installed. Please install Java 11 or higher."
    exit /b 1
)

REM Check if Gradle wrapper exists
if not exist "gradlew.bat" (
    call :print_error "Gradle wrapper not found. Please run this script from the project root."
    exit /b 1
)

REM Check if google-services.json exists
if not exist "app\google-services.json" (
    call :print_warning "google-services.json not found. Please add it to the app\ directory."
)

call :print_success "Requirements check completed."
goto :eof

REM Function to clean project
:clean_project
call :print_status "Cleaning project..."
call gradlew.bat clean
if errorlevel 1 (
    call :print_error "Failed to clean project."
    exit /b 1
)
call :print_success "Project cleaned successfully."
goto :eof

REM Function to run tests
:run_tests
call :print_status "Running tests..."
call gradlew.bat test
if errorlevel 1 (
    call :print_error "Tests failed."
    exit /b 1
)
call :print_success "Tests completed successfully."
goto :eof

REM Function to run instrumented tests
:run_instrumented_tests
call :print_status "Running instrumented tests..."
call gradlew.bat connectedAndroidTest
if errorlevel 1 (
    call :print_error "Instrumented tests failed."
    exit /b 1
)
call :print_success "Instrumented tests completed successfully."
goto :eof

REM Function to build debug APK
:build_debug
call :print_status "Building debug APK..."
call gradlew.bat assembleDebug
if errorlevel 1 (
    call :print_error "Failed to build debug APK."
    exit /b 1
)
call :print_success "Debug APK built successfully."
call :print_status "APK location: app\build\outputs\apk\debug\app-debug.apk"
goto :eof

REM Function to build release APK
:build_release
call :print_status "Building release APK..."

REM Check if keystore exists
if not exist "app\keystore\release.keystore" (
    call :print_warning "Release keystore not found. Building unsigned release APK."
    call gradlew.bat assembleRelease
) else (
    call :print_status "Using release keystore for signing."
    call gradlew.bat assembleRelease
)

if errorlevel 1 (
    call :print_error "Failed to build release APK."
    exit /b 1
)
call :print_success "Release APK built successfully."
call :print_status "APK location: app\build\outputs\apk\release\app-release.apk"
goto :eof

REM Function to build release AAB
:build_aab
call :print_status "Building release AAB..."

REM Check if keystore exists
if not exist "app\keystore\release.keystore" (
    call :print_warning "Release keystore not found. Building unsigned release AAB."
    call gradlew.bat bundleRelease
) else (
    call :print_status "Using release keystore for signing."
    call gradlew.bat bundleRelease
)

if errorlevel 1 (
    call :print_error "Failed to build release AAB."
    exit /b 1
)
call :print_success "Release AAB built successfully."
call :print_status "AAB location: app\build\outputs\bundle\release\app-release.aab"
goto :eof

REM Function to install debug APK on connected device
:install_debug
call :print_status "Installing debug APK on connected device..."

REM Check if device is connected
adb devices | findstr "device$" >nul
if errorlevel 1 (
    call :print_error "No Android device connected. Please connect a device and enable USB debugging."
    exit /b 1
)

call gradlew.bat installDebug
if errorlevel 1 (
    call :print_error "Failed to install debug APK."
    exit /b 1
)
call :print_success "Debug APK installed successfully."
goto :eof

REM Function to run lint
:run_lint
call :print_status "Running lint checks..."
call gradlew.bat lint
if errorlevel 1 (
    call :print_warning "Lint found issues. Check the report for details."
) else (
    call :print_success "Lint checks completed."
)
goto :eof

REM Function to generate build report
:generate_report
call :print_status "Generating build report..."

REM Create reports directory
if not exist "reports" mkdir reports

REM Generate dependency report
call gradlew.bat dependencies > reports\dependencies.txt

call :print_success "Build report generated in reports\ directory."
goto :eof

REM Function to show help
:show_help
echo Aplikasi Posyandu Build Script
echo.
echo Usage: %0 [OPTION]
echo.
echo Options:
echo   clean           Clean the project
echo   test            Run unit tests
echo   instrumented    Run instrumented tests
echo   debug           Build debug APK
echo   release         Build release APK
echo   aab             Build release AAB
echo   install         Install debug APK on connected device
echo   lint            Run lint checks
echo   report          Generate build report
echo   all             Run all checks and build debug APK
echo   help            Show this help message
echo.
echo Examples:
echo   %0 debug        # Build debug APK
echo   %0 release      # Build release APK
echo   %0 all          # Run all checks and build
goto :eof

REM Main script logic
if "%1"=="" goto show_help
if "%1"=="help" goto show_help
if "%1"=="clean" goto clean_project
if "%1"=="test" goto run_tests
if "%1"=="instrumented" goto run_instrumented_tests
if "%1"=="debug" goto build_debug
if "%1"=="release" goto build_release
if "%1"=="aab" goto build_aab
if "%1"=="install" goto install_debug
if "%1"=="lint" goto run_lint
if "%1"=="report" goto generate_report
if "%1"=="all" goto run_all

echo Unknown option: %1
goto show_help

:run_all
call :check_requirements
if errorlevel 1 exit /b 1
call :clean_project
if errorlevel 1 exit /b 1
call :run_tests
if errorlevel 1 exit /b 1
call :run_lint
call :build_debug
if errorlevel 1 exit /b 1
call :generate_report
call :print_success "All tasks completed successfully!"
goto :eof 