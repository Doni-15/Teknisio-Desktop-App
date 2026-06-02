# Teknisio Backend

Backend service untuk aplikasi **Teknisio: Solusi Servis Anda**.

Backend ini digunakan untuk menangani proses:

- Autentikasi user
- Manajemen customer dan teknisi
- Pemesanan layanan servis
- Chat real-time
- Tracking status layanan
- Notifikasi
- Review teknisi
- Manajemen database menggunakan Flyway

---

## Tech Stack

| Teknologi | Versi / Keterangan |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.14 |
| Gradle | Kotlin DSL |
| PostgreSQL | 16 |
| Docker | Untuk menjalankan database lokal |
| Flyway | Database migration |
| JPA / Hibernate | ORM entity Java ke database |
| Spring Security | Security dan authentication |
| JWT | Token authentication |
| WebSocket | Chat real-time |

---

# 1. Tahapan Setup Project yang Sudah Dibuat

Bagian ini menjelaskan struktur dan konfigurasi yang sudah dibuat pada backend Teknisio.

---

## 1.1 Membuat Project Spring Boot

Project backend dibuat menggunakan Spring Boot dengan dependency utama:

- Spring Web
- Spring Data JPA
- Spring Security
- Spring WebSocket
- Spring Validation
- PostgreSQL Driver
- Flyway Migration
- Lombok
- JWT
- Spring Dotenv

File Gradle utama:

```text
build.gradle.kts
```

Dependency penting:

```kotlin
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("org.springframework.boot:spring-boot-starter-websocket")
implementation("org.springframework.boot:spring-boot-starter-validation")

runtimeOnly("org.postgresql:postgresql")

implementation("org.flywaydb:flyway-core")
implementation("org.flywaydb:flyway-database-postgresql")

implementation("io.jsonwebtoken:jjwt-api:0.12.5")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

compileOnly("org.projectlombok:lombok")
annotationProcessor("org.projectlombok:lombok")

implementation("me.paulschwarz:spring-dotenv:4.0.0")
```

---

## 1.2 Konfigurasi Application

Konfigurasi utama berada di:

```text
src/main/resources/application.yml
```

Isi konfigurasi:

```yaml
spring:
  application:
    name: teknisio-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/${POSTGRES_DB:teknisio_db}
    username: ${POSTGRES_USER:teknisio_user}
    password: ${POSTGRES_PASSWORD:teknisio_pass}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    open-in-view: false
    properties:
      hibernate:
        format_sql: true

  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: ${SERVER_PORT:8080}
```

Penjelasan penting:

| Konfigurasi | Fungsi |
|---|---|
| `ddl-auto: validate` | Hibernate hanya mengecek kecocokan entity dengan tabel |
| `flyway.enabled: true` | Database dibuat melalui migration Flyway |
| `locations: classpath:db/migration` | Lokasi file migration |
| `open-in-view: false` | Lebih aman untuk struktur service-layer |
| `${POSTGRES_DB}` | Diambil dari file `.env` |

---

## 1.3 Konfigurasi Environment

File `.env` digunakan untuk menyimpan konfigurasi database lokal.

File:

```text
.env
```

Isi:

```env
This is secret brother
```

Catatan:

- File `.env` tidak boleh di-commit ke GitHub.
- Yang boleh di-commit adalah `.env.example`.

Contoh `.env.example`:

```env
POSTGRES_DB=teknisio_db
POSTGRES_USER=teknisio_user
POSTGRES_PASSWORD=change_me
POSTGRES_PORT=5432
SERVER_PORT=8080
```

---

## 1.4 Konfigurasi Docker PostgreSQL

Database PostgreSQL dijalankan menggunakan Docker agar semua anggota tim memakai environment yang sama.

File:

```text
docker-compose.yml
```

Isi:

```yaml
services:
  postgres:
    image: postgres:16
    container_name: teknisio_db
    env_file:
      - .env
    ports:
      - "${POSTGRES_PORT:-5432}:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-teknisio_user} -d ${POSTGRES_DB:-teknisio_db}"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

---

## 1.5 Database Migration

Project ini menggunakan **Flyway** untuk membuat database schema.

Lokasi migration:

```text
src/main/resources/db/migration
```

Urutan file migration:

```text
V1__create_enums.sql
V2__create_tables.sql
V3__create_indexes.sql
V4__create_triggers.sql
```

Penjelasan:

| File | Fungsi |
|---|---|
| `V1__create_enums.sql` | Membuat enum PostgreSQL |
| `V2__create_tables.sql` | Membuat tabel dan relasi foreign key |
| `V3__create_indexes.sql` | Membuat index untuk mempercepat query |
| `V4__create_triggers.sql` | Membuat function dan trigger database |

Catatan penting:

- Jangan mengubah migration lama jika sudah pernah dijalankan.
- Jika ada perubahan schema, buat file migration baru.
- Contoh file baru:

```text
V5__add_column_example.sql
```

---

## 1.6 Struktur Database Saat Ini

Database saat ini memiliki tabel utama:

```text
users
teknisi_profile
kategori_layanan
jenis_layanan
teknisi_layanan
permintaan_layanan
media_permintaan
pesan
notifikasi
riwayat_status
log_aktivitas
jadwal_teknisi
user_session
review
```

---

## 1.7 Struktur Folder Backend

Struktur folder backend yang digunakan:

```text
src/main/java/com/teknisio/backend
│
├── config/
│   └── konfigurasi Spring Boot, Security, WebSocket, CORS
│
├── controller/
│   └── endpoint REST API
│
├── service/
│   └── business logic aplikasi
│
├── repository/
│   └── akses database menggunakan Spring Data JPA
│
├── model/
│   └── entity JPA
│
├── model/enums/
│   └── enum Java untuk role, status, tipe pesan, dll
│
├── dto/
│   ├── request/
│   └── response/
│
├── security/
│   └── JWT, filter, authentication provider
│
└── exception/
    └── custom exception dan global error handler
```

---

# 2. Tahapan Menjalankan Backend untuk Anggota Tim

Bagian ini digunakan oleh teman satu tim yang ingin menjalankan backend di laptop masing-masing.

---

## 2.1 Install Tools yang Dibutuhkan

Sebelum menjalankan project, pastikan sudah menginstall:

1. Java 17
2. Docker
3. Docker Compose
4. Git
5. IDE, misalnya IntelliJ IDEA atau VS Code

---

## 2.2 Cek Java

Jalankan:

```bash
java -version
```

Harus muncul versi Java 17, contoh:

```bash
openjdk version "17"
```

Jika belum ada Java 17, install terlebih dahulu.

### Arch Linux

```bash
sudo pacman -S jdk17-openjdk
sudo archlinux-java set java-17-openjdk
```

### Ubuntu / Debian

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Cek ulang:

```bash
java -version
```

---

## 2.3 Cek Docker

Jalankan:

```bash
docker --version
docker compose version
```

Kalau Docker belum aktif, jalankan:

```bash
sudo systemctl enable --now docker
```

Tambahkan user ke group Docker:

```bash
sudo usermod -aG docker $USER
```

Setelah itu logout dan login ulang.

---

## 2.4 Clone Repository

Clone project dari repository:

```bash
git clone <url-repository>
```

Masuk ke folder backend:

```bash
cd teknisio-backend
```

Atau sesuaikan dengan nama folder project:

```bash
cd "Teknisio Backend"
```

---

## 2.5 Buat File `.env`

Buat file `.env` di root project.

```bash
touch .env
```

Isi file `.env`:

```env
sudah kita kirim, this is secret brother
```

Pastikan file `.env` berada sejajar dengan:

```text
docker-compose.yml
build.gradle.kts
settings.gradle.kts
```

---

## 2.6 Jalankan PostgreSQL dengan Docker

Jalankan database:

```bash
docker compose up -d
```

Cek apakah container sudah berjalan:

```bash
docker ps
```

Harus muncul container:

```text
teknisio_db
```

---

## 2.7 Cek Koneksi ke PostgreSQL

Masuk ke PostgreSQL:

```bash
docker exec -it teknisio_db psql -U teknisio_user -d teknisio_db
```

Jika berhasil, akan masuk ke console PostgreSQL:

```text
teknisio_db=>
```

Keluar dari PostgreSQL:

```sql
\q
```

---

## 2.8 Jalankan Backend

Setelah database berjalan, jalankan backend:

```bash
./gradlew bootRun
```

Jika menggunakan Windows:

```bash
.\gradlew.bat bootRun
```

Jika berhasil, akan muncul log seperti:

```text
Tomcat started on port 8080
Started BackendApplication
```

Backend berjalan di:

```text
http://localhost:8080
```

---

## 2.9 Cek Flyway Berjalan

Saat backend pertama kali dijalankan, Flyway otomatis menjalankan migration:

```text
V1__create_enums.sql
V2__create_tables.sql
V3__create_indexes.sql
V4__create_triggers.sql
```

Untuk mengecek tabel dari PostgreSQL:

```bash
docker exec -it teknisio_db psql -U teknisio_user -d teknisio_db
```

Lalu jalankan:

```sql
\dt
```

Harus muncul tabel seperti:

```text
users
teknisi_profile
kategori_layanan
jenis_layanan
permintaan_layanan
pesan
review
flyway_schema_history
```

Cek history Flyway:

```sql
SELECT * FROM flyway_schema_history;
```

---

# 3. Flow Cepat Menjalankan Project

Untuk teman yang sudah pernah setup, cukup jalankan:

```bash
docker compose up -d
./gradlew bootRun
```

Untuk Windows:

```bash
docker compose up -d
gradlew.bat bootRun
```

---

# 4. Flow Setup dari Nol

Untuk teman yang baru pertama kali clone project:

```bash
# 1. Clone project
git clone <url-repository>
cd teknisio-backend

# 2. Buat file .env
cp .env.example .env

# 3. Jalankan database
docker compose up -d

# 4. Jalankan backend
./gradlew bootRun
```

Kalau belum ada `.env.example`, buat manual file `.env` dengan isi:

```env
This is secret brother
```

---

# 5. Endpoint Awal

Endpoint yang direncanakan:

| Method | Endpoint | Fungsi |
|---|---|---|
| POST | `/api/auth/register` | Registrasi user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/services` | Melihat daftar layanan |
| POST | `/api/service-requests` | Membuat permintaan layanan |
| GET | `/api/service-requests/me` | Melihat riwayat permintaan customer |
| GET | `/api/technician/requests` | Teknisi melihat permintaan masuk |
| PATCH | `/api/service-requests/{id}/status` | Update status permintaan |
| GET | `/api/notifications` | Melihat notifikasi |
| WS | `/ws` | WebSocket untuk chat real-time |

Catatan:

Endpoint bisa berubah mengikuti implementasi controller.

---

# 6. Docker Commands

## Menjalankan database

```bash
docker compose up -d
```

## Stop database

```bash
docker compose down
```

## Stop dan hapus seluruh data database

```bash
docker compose down -v
```

> Perintah ini akan menghapus volume PostgreSQL. Semua data lokal akan hilang.

## Restart database dari nol

```bash
docker compose down -v
docker compose up -d
```

## Melihat log PostgreSQL

```bash
docker logs teknisio_db
```

## Masuk ke PostgreSQL

```bash
docker exec -it teknisio_db psql -U teknisio_user -d teknisio_db
```

## Melihat daftar tabel

```sql
\dt
```

## Melihat struktur tabel

```sql
\d users
```

## Keluar dari PostgreSQL

```sql
\q
```

---

# 7. Gradle Commands

## Menjalankan backend

```bash
./gradlew bootRun
```

## Build project

```bash
./gradlew build
```

## Menjalankan test

```bash
./gradlew test
```

## Membersihkan build folder

```bash
./gradlew clean
```

## Clean lalu build ulang

```bash
./gradlew clean build
```

---

# 8. Aturan Migration Database

Karena project menggunakan Flyway, ada beberapa aturan penting.

## 8.1 Jangan Edit Migration Lama

Jika file migration sudah pernah dijalankan, jangan edit file tersebut.

Contoh file lama:

```text
V1__create_enums.sql
V2__create_tables.sql
V3__create_indexes.sql
V4__create_triggers.sql
```

Jangan mengubah file-file tersebut jika database teman lain sudah pernah menjalankannya.

---

## 8.2 Buat Migration Baru untuk Perubahan Schema

Jika ingin menambah kolom, tabel, index, atau constraint, buat file baru.

Contoh:

```text
V5__add_user_avatar_column.sql
```

Isi contoh:

```sql
ALTER TABLE users
ADD COLUMN avatar_url TEXT;
```

---

## 8.3 Reset Database Saat Development

Jika masih tahap development dan ingin reset total database lokal:

```bash
docker compose down -v
docker compose up -d
./gradlew bootRun
```

---

# 9. Git Rules

## File yang Tidak Boleh Di-commit

Jangan commit file berikut:

```text
.env
build/
.gradle/
.idea/
*.log
```

## File yang Boleh Di-commit

File berikut boleh dan sebaiknya di-commit:

```text
.env.example
docker-compose.yml
build.gradle.kts
settings.gradle.kts
src/main/resources/application.yml
src/main/resources/db/migration/*.sql
```

---

# 10. Troubleshooting

## 10.1 Docker Permission Denied

Error:

```text
permission denied while trying to connect to the Docker daemon socket
```

Solusi:

```bash
sudo usermod -aG docker $USER
```

Lalu logout dan login ulang.

---

## 10.2 Port 5432 Sudah Dipakai

Error:

```text
Bind for 0.0.0.0:5432 failed: port is already allocated
```

Solusi 1: stop PostgreSQL lokal.

```bash
sudo systemctl stop postgresql
```

Solusi 2: ganti port di `.env`.

```env
POSTGRES_PORT=5433
```

Lalu jalankan ulang:

```bash
docker compose down
docker compose up -d
```

Jika port diganti ke 5433, ubah juga `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/${POSTGRES_DB:teknisio_db}
```

---

## 10.3 Container Sudah Ada

Jika muncul error container sudah ada:

```bash
docker rm -f teknisio_db
docker compose up -d
```

---

## 10.4 Database Credential Salah

Error:

```text
password authentication failed for user "teknisio_user"
```

Solusi:

Pastikan `.env` sama dengan konfigurasi Spring Boot.

```env
POSTGRES_DB=teknisio_db
POSTGRES_USER=teknisio_user
POSTGRES_PASSWORD=teknisio_pass
```

Jika sebelumnya pernah membuat container dengan credential berbeda, reset volume:

```bash
docker compose down -v
docker compose up -d
```

---

## 10.5 Flyway Error karena Migration Pernah Berubah

Error biasanya seperti:

```text
Validate failed: Migration checksum mismatch
```

Solusi saat development:

```bash
docker compose down -v
docker compose up -d
./gradlew bootRun
```

Catatan:

Ini aman untuk development lokal, tapi jangan dilakukan di production.

---

## 10.6 Hibernate Validate Error

Error biasanya seperti:

```text
Schema-validation: wrong column type encountered
```

Penyebab:

Entity Java tidak cocok dengan schema database.

Solusi:

- Cek nama tabel
- Cek nama kolom
- Cek tipe data
- Cek enum PostgreSQL
- Pastikan migration sudah jalan

---

## 10.7 Aplikasi Minta Login Basic

Jika saat buka backend muncul login basic atau password random di terminal, itu normal karena dependency Spring Security sudah aktif.

Nanti security akan diganti menggunakan JWT authentication.

---

# 11. Catatan untuk Developer

- Jalankan Docker PostgreSQL sebelum menjalankan Spring Boot.
- Jangan hardcode credential database di source code.
- Gunakan `.env` untuk konfigurasi lokal.
- Jangan commit file `.env`.
- Gunakan Flyway untuk semua perubahan database.
- Jangan edit migration lama yang sudah dijalankan.
- Gunakan migration baru jika ada perubahan schema.
- Pastikan Java yang digunakan adalah Java 17.
- Pastikan database container `teknisio_db` berjalan sebelum `./gradlew bootRun`.
