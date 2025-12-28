#!/bin/bash

# Build Script for Aplikasi Posyandu
# This script automates the build process for different build types

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if required tools are installed
check_requirements() {
    print_status "Checking requirements..."
    
    # Check if Java is installed
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 11 or higher."
        exit 1
    fi
    
    # Check if Gradle is available
    if ! command -v ./gradlew &> /dev/null; then
        print_error "Gradle wrapper not found. Please run this script from the project root."
        exit 1
    fi
    
    # Check if google-services.json exists
    if [ ! -f "app/google-services.json" ]; then
        print_warning "google-services.json not found. Please add it to the app/ directory."
    fi
    
    print_success "Requirements check completed."
}

# Function to clean project
clean_project() {
    print_status "Cleaning project..."
    ./gradlew clean
    print_success "Project cleaned successfully."
}

# Function to run tests
run_tests() {
    print_status "Running tests..."
    ./gradlew test
    print_success "Tests completed successfully."
}

# Function to run instrumented tests
run_instrumented_tests() {
    print_status "Running instrumented tests..."
    ./gradlew connectedAndroidTest
    print_success "Instrumented tests completed successfully."
}

# Function to build debug APK
build_debug() {
    print_status "Building debug APK..."
    ./gradlew assembleDebug
    print_success "Debug APK built successfully."
    print_status "APK location: app/build/outputs/apk/debug/app-debug.apk"
}

# Function to build release APK
build_release() {
    print_status "Building release APK..."
    
    # Check if keystore exists
    if [ ! -f "app/keystore/release.keystore" ]; then
        print_warning "Release keystore not found. Building unsigned release APK."
        ./gradlew assembleRelease
    else
        print_status "Using release keystore for signing."
        ./gradlew assembleRelease
    fi
    
    print_success "Release APK built successfully."
    print_status "APK location: app/build/outputs/apk/release/app-release.apk"
}

# Function to build release AAB (Android App Bundle)
build_aab() {
    print_status "Building release AAB..."
    
    # Check if keystore exists
    if [ ! -f "app/keystore/release.keystore" ]; then
        print_warning "Release keystore not found. Building unsigned release AAB."
        ./gradlew bundleRelease
    else
        print_status "Using release keystore for signing."
        ./gradlew bundleRelease
    fi
    
    print_success "Release AAB built successfully."
    print_status "AAB location: app/build/outputs/bundle/release/app-release.aab"
}

# Function to install debug APK on connected device
install_debug() {
    print_status "Installing debug APK on connected device..."
    
    # Check if device is connected
    if ! adb devices | grep -q "device$"; then
        print_error "No Android device connected. Please connect a device and enable USB debugging."
        exit 1
    fi
    
    ./gradlew installDebug
    print_success "Debug APK installed successfully."
}

# Function to run lint
run_lint() {
    print_status "Running lint checks..."
    ./gradlew lint
    print_success "Lint checks completed."
}

# Function to generate build report
generate_report() {
    print_status "Generating build report..."
    
    # Create reports directory
    mkdir -p reports
    
    # Generate dependency report
    ./gradlew dependencies > reports/dependencies.txt
    
    # Generate build scan (if available)
    if command -v ./gradlew buildScan &> /dev/null; then
        ./gradlew buildScan
    fi
    
    print_success "Build report generated in reports/ directory."
}

# Function to show help
show_help() {
    echo "Aplikasi Posyandu Build Script"
    echo ""
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  clean           Clean the project"
    echo "  test            Run unit tests"
    echo "  instrumented    Run instrumented tests"
    echo "  debug           Build debug APK"
    echo "  release         Build release APK"
    echo "  aab             Build release AAB"
    echo "  install         Install debug APK on connected device"
    echo "  lint            Run lint checks"
    echo "  report          Generate build report"
    echo "  all             Run all checks and build debug APK"
    echo "  help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 debug        # Build debug APK"
    echo "  $0 release      # Build release APK"
    echo "  $0 all          # Run all checks and build"
}

# Main script logic
case "${1:-help}" in
    "clean")
        check_requirements
        clean_project
        ;;
    "test")
        check_requirements
        run_tests
        ;;
    "instrumented")
        check_requirements
        run_instrumented_tests
        ;;
    "debug")
        check_requirements
        clean_project
        run_tests
        build_debug
        ;;
    "release")
        check_requirements
        clean_project
        run_tests
        run_lint
        build_release
        ;;
    "aab")
        check_requirements
        clean_project
        run_tests
        run_lint
        build_aab
        ;;
    "install")
        check_requirements
        build_debug
        install_debug
        ;;
    "lint")
        check_requirements
        run_lint
        ;;
    "report")
        check_requirements
        generate_report
        ;;
    "all")
        check_requirements
        clean_project
        run_tests
        run_lint
        build_debug
        generate_report
        print_success "All tasks completed successfully!"
        ;;
    "help"|*)
        show_help
        ;;
esac 