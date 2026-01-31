/*
 * ESP32-C6 Code untuk Aplikasi Posyandu
 * Komunikasi dengan aplikasi via Bluetooth Low Energy (BLE) GATT
 * Sensor: HX711 Load Cell (berat) dan VL53L0X (tinggi)
 * Display: LCD 20x4 I2C
 * LED RGB untuk indikasi status koneksi
 *
 * Konfigurasi Sensor:
 * - Sensor tinggi (VL53L0X) dipasang di atas dengan ketinggian rangka 1.5 meter
 * - Sensor berat (Load Cell) dipasang di bawah
 * - Tersedia fungsi kalibrasi untuk kedua sensor
 *
 * Perintah Serial:
 * - "calibrate" - Kalibrasi sensor berat
 * - "calibrate_height" - Kalibrasi sensor tinggi
 * - "tare" - Reset skala berat ke 0
 * - "set_height_frame 150.0" - Atur ketinggian rangka sensor (cm)
 *
 * Komunikasi BLE:
 * - Service UUID: BLE_SERVICE_UUID
 * - Characteristic Data (Notify): berat, tinggi, timestamp (JSON)
 * - Characteristic Command (Write): "calibrate", "calibrate_height", "tare",
 *   dan "set_height_frame <cm>"
 *
 * Indikator LED:
 * - Merah berkedip: error
 * - Kuning berkedip: advertising/menunggu koneksi BLE
 * - Hijau solid: terhubung dan siap
 * - Hijau berkedip: sedang mengirim data via BLE
 */

#include <NimBLEDevice.h>
#include <Wire.h>
#include <VL53L0X.h>
#include <HX711.h>
#include <LiquidCrystal_I2C.h>

// Definisi pin dan konstanta sudah dipindahkan ke bawah

// BLE UUIDs
#define BLE_SERVICE_UUID       "e8c15bd0-5f6d-4f0c-9f2a-1a2b3c4d5e60"
#define BLE_DATA_CHAR_UUID     "e8c15bd1-5f6d-4f0c-9f2a-1a2b3c4d5e60" // Notify sensor data
#define BLE_COMMAND_CHAR_UUID  "e8c15bd2-5f6d-4f0c-9f2a-1a2b3c4d5e60" // Write commands

// RGB LED pin (menggunakan LED RGB dengan satu pin kontrol)
#define LED_RGB_PIN 8
int ledBrightness = 50; // Default brightness (0-255)

// Variabel untuk status LED berkedip
unsigned long lastBlinkTime = 0;
const int blinkInterval = 500; // Interval kedip dalam milidetik
bool ledState = false; // Status LED untuk efek berkedip

// BLE objects & state
NimBLEServer* bleServer = nullptr;
NimBLEService* bleService = nullptr;
NimBLECharacteristic* bleDataChar = nullptr;
NimBLECharacteristic* bleCommandChar = nullptr;
volatile bool bleHasCommand = false;
String blePendingCommand = "";
bool bleConnected = false;

class ServerCallbacks : public NimBLEServerCallbacks {
  void onConnect(NimBLEServer* pServer) override {
    bleConnected = true;
    Serial.println("BLE connected");
    updateStatusLED("connected");
  }
  void onDisconnect(NimBLEServer* pServer) override {
    bleConnected = false;
    Serial.println("BLE disconnected, restarting advertising");
    updateStatusLED("connecting");
    NimBLEDevice::getAdvertising()->start();
  }
};

class CommandCallbacks : public NimBLECharacteristicCallbacks {
  void onWrite(NimBLECharacteristic* pCharacteristic) override {
    std::string value = pCharacteristic->getValue();
    blePendingCommand = String(value.c_str());
    blePendingCommand.trim();
    bleHasCommand = true;
    Serial.printf("BLE command received: %s\n", blePendingCommand.c_str());
  }
};

// VL53L0X sensor
VL53L0X sensor;

// HX711 Load Cell pins (ESP32-C6 compatible)
#define LOADCELL_DOUT_PIN 4
#define LOADCELL_SCK_PIN 5

// HX711 Load Cell
HX711 scale;

// LCD 20x4 I2C (address 0x27 is common, adjust if needed)
LiquidCrystal_I2C lcd(0x27, 20, 4);

// Calibration values for Load Cell
// CATATAN: Timbangan akan otomatis zero (tare) saat startup seperti timbangan digital pada umumnya
// Untuk kalibrasi akurasi (opsional), gunakan perintah "calibrate" via Serial/BLE
// Setelah dikalibrasi, catat nilai calibration_factor dan masukkan di sini untuk penggunaan permanen
float calibration_factor = -7050; // Faktor kalibrasi (sesuaikan dengan load cell Anda)
float known_weight = 1.0; // Berat yang diketahui untuk kalibrasi (kg)
long zero_factor = 0; // Nilai pembacaan load cell saat kosong (auto-set saat startup)

// Sensor tinggi configuration
float sensor_height_frame = 150.0f; // Ketinggian rangka sensor tinggi (cm) - 1.5 meter
float height_offset = 0.0f; // Offset kalibrasi untuk sensor tinggi (cm)

// Variables
float berat = 0.0;
float tinggi = 0.0;
unsigned long lastUpdate = 0;
const unsigned long updateInterval = 5000; // Update setiap 5 detik

// Fungsi setRGB telah dihapus dan diganti dengan penggunaan langsung analogWrite ke LED_RGB_PIN

// Fungsi untuk mengatur status LED berdasarkan status koneksi
void updateStatusLED(String status) {
  unsigned long currentMillis = millis();
  
  if (status == "connected") {
    // Hijau solid untuk siap digunakan
    analogWrite(LED_RGB_PIN, 150 * ledBrightness / 255); // Hijau - level PWM sedang
  } 
  else if (status == "connecting") {
    // Kuning berkedip untuk proses koneksi ke database
    if (currentMillis - lastBlinkTime >= blinkInterval) {
      lastBlinkTime = currentMillis;
      ledState = !ledState;
      
      if (ledState) {
        analogWrite(LED_RGB_PIN, 200 * ledBrightness / 255); // Kuning - level PWM tinggi
      } else {
        analogWrite(LED_RGB_PIN, 0); // Mati
      }
    }
  } 
  else if (status == "sending") {
    // Hijau berkedip saat mengirim data ke database
    if (currentMillis - lastBlinkTime >= blinkInterval) {
      lastBlinkTime = currentMillis;
      ledState = !ledState;
      
      if (ledState) {
        analogWrite(LED_RGB_PIN, 150 * ledBrightness / 255); // Hijau - level PWM sedang
      } else {
        analogWrite(LED_RGB_PIN, 0); // Mati
      }
    }
  }
  else if (status == "disconnected") {
    // Merah solid untuk tidak terhubung
    analogWrite(LED_RGB_PIN, 50 * ledBrightness / 255); // Merah - level PWM rendah
  }
  else if (status == "error") {
    // Merah berkedip untuk error
    if (currentMillis - lastBlinkTime >= blinkInterval) {
      lastBlinkTime = currentMillis;
      ledState = !ledState;
      
      if (ledState) {
        analogWrite(LED_RGB_PIN, 50 * ledBrightness / 255); // Merah - level PWM rendah
      } else {
        analogWrite(LED_RGB_PIN, 0); // Mati
      }
    }
  }
}

void setup() {
  // Initialize serial communication
  Serial.begin(115200);
  Serial.println("ESP32-C6 Posyandu System");
  
  // Initialize RGB LED pin
  pinMode(LED_RGB_PIN, OUTPUT);
  
  // Set initial LED color to red blinking (not connected/initializing)
  updateStatusLED("error");
  
  // Initialize LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("ESP32-C6 Posyandu");
  lcd.setCursor(0, 1);
  lcd.print("Initializing...");
  
  // Set default LED brightness (0-255)
  setBrightness(50); // Atur kecerahan LED RGB
  
  // Initialize HX711 Load Cell
  Serial.println("Initializing load cell...");
  scale.begin(LOADCELL_DOUT_PIN, LOADCELL_SCK_PIN);
  
  // Set calibration factor
  scale.set_scale(calibration_factor);
  
  // Auto-tare: Reset scale to 0 (seperti timbangan digital pada umumnya)
  Serial.println("Auto-zeroing scale (tare)...");
  scale.tare(10); // Average 10 readings untuk akurasi lebih baik
  zero_factor = scale.read_average(10); // Simpan nilai zero untuk referensi
  
  Serial.println("Load cell ready!");
  Serial.printf("Calibration factor: %.2f\n", calibration_factor);
  Serial.printf("Zero factor: %ld\n", zero_factor);
  Serial.println("Scale automatically zeroed. Ready to weigh.");
  
  // Initialize VL53L0X sensor
  Wire.begin();
  if (!sensor.init()) {
    Serial.println("Failed to initialize VL53L0X!");
    lcd.setCursor(0, 1);
    lcd.print("VL53L0X Error!     ");
  }
  sensor.setTimeout(500);
  sensor.startContinuous();
  Serial.printf("Height sensor initialized with frame height: %.2f cm\n", sensor_height_frame);
  Serial.printf("Height offset: %.2f cm\n", height_offset);
  
  // Initialize BLE (advertising for app connection)
  Serial.println("Initializing BLE...");
  lcd.setCursor(0, 1);
  lcd.print("BLE Init...        ");

  NimBLEDevice::init("E-POSYANDU");
  bleServer = NimBLEDevice::createServer();
  bleServer->setCallbacks(new ServerCallbacks());

  bleService = bleServer->createService(BLE_SERVICE_UUID);
  bleDataChar = bleService->createCharacteristic(
    BLE_DATA_CHAR_UUID,
    NIMBLE_PROPERTY::READ | NIMBLE_PROPERTY::NOTIFY
  );
  bleCommandChar = bleService->createCharacteristic(
    BLE_COMMAND_CHAR_UUID,
    NIMBLE_PROPERTY::WRITE
  );
  bleCommandChar->setCallbacks(new CommandCallbacks());

  bleService->start();
  NimBLEAdvertising* adv = NimBLEDevice::getAdvertising();
  adv->addServiceUUID(BLE_SERVICE_UUID);
  adv->setScanResponse(true);
  adv->start();
  Serial.println("BLE advertising started");
  lcd.setCursor(0, 1);
  lcd.print("BLE Advertising... ");
  
  // Set LED to yellow blinking (waiting for BLE connection)
  updateStatusLED("connecting");
  
  // Initialize I2C for ESP32-C6 (SDA=6, SCL=7)
  Wire.begin(6, 7);
  
  // Initialize LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("ESP32-C6 Posyandu");
  lcd.setCursor(0, 1);
  lcd.print("Initializing...");
  
  // Set default LED brightness (0-255)
  setBrightness(50); // Atur kecerahan LED RGB
  
  // Initialize HX711 Load Cell (duplikat - sudah diinisialisasi di atas)
  // Kode ini duplikat dan tidak perlu dijalankan lagi
  
  // Initialize VL53L0X sensor
  sensor.setTimeout(500);
  if (!sensor.init()) {
    Serial.println("Failed to detect and initialize VL53L0X sensor!");
    lcd.setCursor(0, 2);
    lcd.print("VL53L0X Error!");
    while (1);
  }
  
  // Set measurement timing budget
  sensor.setMeasurementTimingBudget(200000);
  
  lcd.setCursor(0, 2);
  lcd.print("Sensors OK!");
  delay(2000);
  lcd.clear();
  
  Serial.println("ESP32 Posyandu Sensor initialized successfully!");
}

void loop() {
  unsigned long currentTime = millis();
  
  // Update LED status berdasarkan status BLE
  if (bleConnected) {
    updateStatusLED("connected");
  } else {
    updateStatusLED("connecting"); // advertising, menunggu koneksi dari aplikasi
  }
  
  if (currentTime - lastUpdate >= updateInterval) {
    // Read sensor data
    readSensors();
    
    // Kirim data ke aplikasi via BLE
    sendToBLE();
    
    lastUpdate = currentTime;
  }
  
  // Check for serial commands
  if (Serial.available()) {
    String command = Serial.readStringUntil('\n');
    command.trim();
    
    if (command == "calibrate") {
      calibrateLoadCell();
    } else if (command == "calibrate_height") {
      calibrateHeightSensor();
    } else if (command == "tare") {
      scale.tare();
      Serial.println("Scale tared");
    } else if (command.startsWith("set_height_frame ")) {
      // Format: set_height_frame 150.0
      float new_height = command.substring(17).toFloat();
      if (new_height > 0) {
        sensor_height_frame = new_height;
        Serial.printf("Height frame set to: %.2f cm\n", sensor_height_frame);
      }
    }
  }
  
  // Process command yang diterima via BLE characteristic
  if (bleHasCommand) {
    String command = blePendingCommand;
    bleHasCommand = false;
    
    if (command == "calibrate") {
      calibrateLoadCell();
    } else if (command == "calibrate_height") {
      calibrateHeightSensor();
    } else if (command == "tare") {
      scale.tare();
      Serial.println("Scale tared");
    } else if (command.startsWith("set_height_frame ")) {
      float new_height = command.substring(17).toFloat();
      if (new_height > 0) {
        sensor_height_frame = new_height;
        Serial.printf("Height frame set to: %.2f cm\n", sensor_height_frame);
      }
    }
  }
}

void readSensors() {
  // Read weight from Load Cell
  berat = readWeight();
  
  // Read height from VL53L0X
  tinggi = readHeight();
  
  // Display on Serial
  Serial.printf("Berat: %.2f kg, Tinggi: %.2f cm\n", berat, tinggi);
  
  // Display on LCD
  updateLCD();
}

float readWeight() {
  // Read weight from HX711 Load Cell
  if (scale.is_ready()) {
    // Baca berat (sudah otomatis di-zero karena tare saat startup)
    float weight = scale.get_units(10); // Average of 10 readings untuk stabilitas
    
    // Pastikan nilai tidak negatif (noise kecil bisa menyebabkan nilai negatif kecil)
    if (weight < 0.05f) { // Threshold 50 gram untuk menghindari noise
      return 0.0f;
    }
    
    return weight;
  } else {
    Serial.println("HX711 not ready");
    return 0.0f;
  }
}

float readHeight() {
  // Read distance from VL53L0X sensor
  uint16_t distance = sensor.readRangeSingleMillimeters();
  
  if (sensor.timeoutOccurred()) {
    Serial.print("VL53L0X timeout!");
    return 0.0f;
  }
  
  // Convert distance to height (sensor mounted at sensor_height_frame)
  // Sensor mengukur jarak dari atas ke bawah (ke kepala)
  float height = sensor_height_frame - (distance / 10.0f) + height_offset;
  return (height > 0.0f) ? height : 0.0f; // Ensure non-negative height
}

void sendToBLE() {
  if (!bleConnected) {
    lcd.setCursor(0, 3);
    lcd.print("BLE: Not Connected ");
    updateStatusLED("connecting");
    return;
  }
  
  // Set LED to green blinking (sending data)
  updateStatusLED("sending");
  
  // Compose JSON string for data payload
  String payload = "{";
  payload += "\"berat\":" + String(berat, 2) + ",";
  payload += "\"tinggi\":" + String(tinggi, 1) + ",";
  payload += "\"timestamp\":" + String(millis());
  payload += "}";
  
  bleDataChar->setValue(payload.c_str());
  bleDataChar->notify();
  Serial.println("Data sent via BLE notify");
  lcd.setCursor(0, 3);
  lcd.print("BLE: Sent OK       ");
  
  // Kembali ke status hijau solid (ready)
  updateStatusLED("connected");
}

void updateLCD() {
  // Clear and update LCD display
  lcd.setCursor(0, 0);
  lcd.print("ESP32-C6 Posyandu  ");
  
  lcd.setCursor(0, 1);
  lcd.print("Berat: ");
  lcd.print(berat, 2);
  lcd.print(" kg    ");
  
  lcd.setCursor(0, 2);
  lcd.print("Tinggi: ");
  lcd.print(tinggi, 1);
  lcd.print(" cm   ");
  
  // BLE status indicator on LCD
  lcd.setCursor(15, 0);
  if (bleConnected) {
    lcd.print("BLE ");
  } else {
    lcd.print("----");
  }
}

// Function to calibrate height sensor
void calibrateHeightSensor() {
  Serial.println("Starting height sensor calibration...");
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Height Calibration");
  lcd.setCursor(0, 1);
  lcd.print("Place 100cm object");
  lcd.setCursor(0, 2);
  lcd.print("Press any key...");
  
  while (!Serial.available()) {
    delay(100);
  }
  Serial.read();
  
  // Baca jarak saat ini
  uint16_t distance = sensor.readRangeSingleMillimeters();
  if (sensor.timeoutOccurred()) {
    Serial.println("VL53L0X timeout during calibration!");
    lcd.setCursor(0, 2);
    lcd.print("Sensor timeout!   ");
    delay(2000);
    return;
  }
  
  // Hitung offset berdasarkan objek 100cm
  float measured_height = sensor_height_frame - (distance / 10.0f);
  height_offset = 100.0f - measured_height; // Seharusnya 100cm
  
  Serial.printf("Measured height: %.2f cm\n", measured_height);
  Serial.printf("Height offset set to: %.2f cm\n", height_offset);
  
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Height Calibrated!");
  lcd.setCursor(0, 1);
  lcd.printf("Offset: %.2f cm", height_offset);
  delay(2000);
  lcd.clear();
}

// Function to calibrate the load cell
void calibrateLoadCell() {
  Serial.println("Starting load cell calibration...");
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Calibration Mode");
  lcd.setCursor(0, 1);
  lcd.print("Remove all weight");
  lcd.setCursor(0, 2);
  lcd.print("Press any key...");
  
  while (!Serial.available()) {
    delay(100);
  }
  Serial.read();
  
  // Simpan nilai zero factor (pembacaan saat kosong)
  zero_factor = scale.read_average(10);
  Serial.printf("Zero factor: %ld\n", zero_factor);
  
  scale.tare(10); // Tare dengan 10 readings untuk akurasi
  Serial.println("Tare complete");
  
  lcd.setCursor(0, 1);
  lcd.print("Place known weight  ");
  lcd.setCursor(0, 2);
  lcd.print("Press any key...    ");
  
  while (!Serial.available()) {
    delay(100);
  }
  Serial.read();
  
  float reading = scale.get_units(10);
  calibration_factor = reading / known_weight;
  scale.set_scale(calibration_factor);
  
  // Reset weight offset
  weight_offset = 0.0f;
  
  Serial.printf("Calibration factor: %.2f\n", calibration_factor);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Calibration Done!");
  lcd.setCursor(0, 1);
  lcd.printf("Factor: %.2f", calibration_factor);
  delay(2000);
  lcd.clear();
}

// Fungsi untuk mengatur kecerahan LED RGB
void setBrightness(int brightness) {
  // Batasi nilai kecerahan antara 0-255
  ledBrightness = constrain(brightness, 0, 255);
  
  // Update LED dengan kecerahan baru berdasarkan status BLE
  if (bleConnected) {
    updateStatusLED("connected");
  } else {
    updateStatusLED("connecting");
  }
  
  Serial.printf("LED brightness set to: %d\n", ledBrightness);
}

/*
 * Catatan Implementasi ESP32-C6:
 * 
 * 1. Pin Configuration:
 *    - I2C: SDA=6, SCL=7 (default untuk ESP32-C6)
 *    - HX711: DOUT=4, SCK=5
 *    - VL53L0X: menggunakan I2C (SDA=6, SCL=7)
 *    - LCD: I2C address 0x27 (sesuaikan jika berbeda)
 *    - RGB LED: Pin 8 (single pin control)
 * 
 * 2. Library Dependencies:
 *    - HX711 library by Bogdan Necula
 *    - VL53L0X library by Pololu
 *    - LiquidCrystal_I2C library
 *    - Firebase ESP Client library
 * 
 * 3. Kalibrasi Load Cell:
 *    - Jalankan fungsi calibrateLoadCell() untuk kalibrasi
 *    - Sesuaikan calibration_factor berdasarkan load cell Anda
 *    - Gunakan berat yang diketahui untuk kalibrasi
 * 
 * 4. Setup Hardware:
 *    - Pastikan koneksi I2C benar (pull-up resistor 4.7kΩ)
 *    - VL53L0X membutuhkan 3.3V (jangan gunakan 5V)
 *    - HX711 dapat menggunakan 3.3V atau 5V
 *    - LED RGB dengan kontrol satu pin (WS2812/NeoPixel atau sejenisnya)
 * 
 * 5. Firebase Setup:
 *    - Ganti API_KEY dan DATABASE_URL
 *    - Aktifkan Realtime Database
 *    - Set rules: {"rules": {".read": true, ".write": true}}
 * 
 * 6. WiFi Configuration:
 *    - Ganti SSID dan password WiFi
 *    - ESP32-C6 mendukung WiFi 6 (802.11ax)
 * 
 * 7. LED RGB Status:
 *    - Hijau: Terhubung ke WiFi dan Firebase
 *    - Kuning berkedip: Proses koneksi atau mengirim data
 *    - Merah: Tidak terhubung atau error
 *    - Kecerahan dapat diatur dengan fungsi setBrightness(0-255)
 * 
 * 8. Troubleshooting:
 *    - Jika LCD tidak muncul, cek alamat I2C dengan I2C scanner
 *    - Jika HX711 tidak stabil, tambahkan kapasitor 100nF
 *    - Jika VL53L0X timeout, cek koneksi I2C dan power supply
 *    - Jika LED RGB tidak menyala, cek koneksi pin dan polaritas LED
 */