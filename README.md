# Teknisio - Platform Layanan Perbaikan Elektronik
Kelompok: 4 Sehat 5 Sempurna
Anggota:
Rifki Al Sauqy (241401007)
Doni Rivaldo Simamora (241401037)
Rionaldo Benedictus Purba (241401064)
Yehezkiel Gustav Setiawan Sitanggang (241401070)
M.Farhan Prasetyo (241401094)

**Teknisio** adalah aplikasi berbasis Java (JavaFX untuk klien Desktop dan Spring Boot untuk Backend) yang menghubungkan pelanggan dengan teknisi perbaikan alat elektronik rumah tangga terdekat. Aplikasi ini dikembangkan untuk meningkatkan **produktivitas dan efisiensi waktu** melalui otomatisasi koordinasi alur kerja servis, pelacakan perjalanan teknisi secara instan, serta integrasi komunikasi langsung di dalam satu sistem terpadu.

---

## Fitur-Fitur Utama

1. **Autentikasi Akun Multi-Peran (Role-Based Access):**
   * Pembedaan akses antara **Pelanggan** (*Customer*) dan **Teknisi** (*Technician*).
   * Pengamanan password menggunakan enkripsi BCrypt dan otorisasi berbasis JWT token.
2. **Manajemen Pemesanan Servis (Service Request Lifecycle):**
   * Pelanggan dapat membuat pemesanan perbaikan dengan memilih kategori perangkat (seperti AC, Kulkas, Mesin Cuci, dll.), mengisi deskripsi kerusakan, dan menugaskan teknisi.
   * Teknisi dapat menerima (*accept*), menolak (*reject*), memulai (*start*), dan menyelesaikan (*complete*) permintaan servis.
   * Riwayat status direkam secara dinamis menggunakan trigger database H2.
3. **Pelacakan Lokasi GPS Real-Time (GPS Tracking):**
   * Pemantauan lokasi pergerakan teknisi di peta interaktif Leaflet.js melalui komponen JavaFX WebView.
   * Mendukung koneksi serial port (COM Port) menggunakan pustaka `jSerialComm` untuk membaca dan mengurai koordinat dari modul GPS fisik (NMEA `$GPRMC`/`$GPGGA` sentences).
   * Dilengkapi fitur simulasi klik pada peta untuk mempermudah penyesuaian titik posisi tanpa hardware.
4. **Chat Terintegrasi (In-App Chat):**
   * Saluran komunikasi teks langsung antara pelanggan dan teknisi yang aktif otomatis saat status servis berada pada fase pengerjaan (`ACCEPTED` atau `ON_PROGRESS`).
5. **Kalkulasi Bisnis Dinamis:**
   * Perhitungan estimasi biaya awal dan total biaya akhir perbaikan berdasarkan kategori alat secara otomatis.
6. **Nota Digital Otomatis (File I/O):**
   * Pembuatan berkas bukti transaksi pengerjaan berformat `.txt` secara otomatis setelah pesanan selesai pengerjaan.
7. **Portal Informasi & Tips (News Feed):**
   * Menyediakan kumpulan artikel informatif seputar tips perawatan mandiri barang elektronik.
8. **Live Search & Sorting Tabel:**
   * Penyaringan dan pencarian data riwayat pemesanan secara instan pada komponen TableView.

---

## Kebutuhan Sistem & Dependencies

### Prerequisites
* **Java Development Kit (JDK) 17** atau versi lebih baru.
* **Apache Maven** (untuk manajemen modul Klien Desktop).
* **Gradle** (untuk manajemen modul Backend Spring Boot).

### Dependencies Klien Desktop (`teknisio/pom.xml`)
* JavaFX (Controls, FXML, Web) v22.
* GSON (Google Code Gson) v2.11.0 untuk serialisasi JSON.
* jSerialComm v2.10.4 untuk komunikasi serial port GPS.

### Dependencies Backend (`teknisio_backend/build.gradle.kts`)
* Spring Boot Starter (Web, Security, Validation, Actuator, WebSocket).
* H2 Database (File-based local database).
* Flyway Core untuk migrasi skema database.
* JSON Web Token (JJWT Api, Impl, Jackson) v0.12.6.
* Lombok.

---

## Cara Menjalankan Aplikasi

### 1. Menjalankan Backend (`teknisio_backend`)
Backend bertindak sebagai penyedia REST API dan database engine lokal menggunakan H2.

1. Buka terminal pada direktori `teknisio_backend`.
2. Duplikat file konfigurasi `.env.example` menjadi `.env` dan sesuaikan parameter koneksi database H2 lokal (opsional, default sudah terkonfigurasi dengan aman):
   ```properties
   H2_URL=jdbc:h2:file:./data/teknisio_db
   H2_USER=sa
   H2_PASSWORD=
   JWT_SECRET=your_jwt_secret_key_here
   ```
3. Jalankan server Spring Boot menggunakan Gradle:
   * **Windows:**
     ```bash
     .\gradlew.bat bootRun
     ```
   * **Linux/macOS:**
     ```bash
     ./gradlew bootRun
     ```
4. Server backend akan aktif di port `http://localhost:8080`. Flyway akan otomatis membuat berkas database H2 di folder `./data/` dan menjalankan migrasi skema database `V1` s.d `V6`.

### 2. Menjalankan Klien Desktop (`teknisio`)
Aplikasi Klien Desktop dikembangkan dengan JavaFX dan berinteraksi langsung ke REST API Backend.

1. Buka terminal baru pada direktori `teknisio`.
2. Bersihkan dan unduh seluruh dependencies melalui Maven:
   ```bash
   mvn clean compile
   ```
3. Jalankan aplikasi klien JavaFX:
   ```bash
   mvn javafx:run
   ```
4. Jendela aplikasi Teknisio akan muncul dan siap digunakan untuk registrasi, login, pemesanan, chat, hingga tracking GPS.

---

## Video Presentasi Aplikasi

Silakan saksikan penjelasan alur aplikasi, integrasi hardware GPS, dan demo fungsionalitas Teknisio melalui tautan YouTube berikut:

[![Video Presentasi Teknisio](https://img.youtube.com/vi/-QjFn0lsS0U/0.jpg)](https://youtu.be/-QjFn0lsS0U?si=qLF7VqJFPzYBQMLa)
