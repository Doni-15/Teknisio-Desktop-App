# Teknisio Backend

Backend service untuk **Teknisio: Solusi Servis Anda**.

Backend ini menjadi satu server utama untuk semua client Teknisio, baik **JavaFX Desktop**, **Android Java**, maupun client lain yang menggunakan REST API. Seluruh komunikasi client ke server menggunakan **HTTP REST API** dengan format **JSON**.

---

## Status Singkat Project

```text
Status backend saat ini: MVP core workflow stabilization
```

Yang sudah stabil sampai strict regression test V5:

```text
✅ Auth dasar: register, login, profile
✅ Role-based access CUSTOMER / TECHNICIAN / ADMIN
✅ Device category public API
✅ Technician device category / skill API
✅ Customer technician discovery
✅ Customer service request create/list/detail/cancel
✅ Technician service request list/detail/accept/reject/start/complete
✅ Status transition divalidasi database trigger
✅ Status history dibuat otomatis database trigger
✅ Strict smoke test V5: 736 passed, 0 failed
```

Yang sudah tersedia di kode tetapi masih perlu dikunci dengan strict smoke test lanjutan:

```text
🟡 Service request status history read API
🟡 Customer create review API
🟡 Review schema, entity, repository, dan response
```

Yang belum menjadi prioritas awal MVP:

```text
⏳ Refresh token dan logout server-side
⏳ Chat REST/WebSocket
⏳ Notification
⏳ Admin management
```

---

## Table of Contents

1. [Tujuan Backend](#1-tujuan-backend)
2. [Tech Stack](#2-tech-stack)
3. [Arsitektur Backend](#3-arsitektur-backend)
4. [Struktur Folder](#4-struktur-folder)
5. [Aturan Kontrak API](#5-aturan-kontrak-api)
6. [Environment Variables](#6-environment-variables)
7. [Menjalankan Project dari Nol](#7-menjalankan-project-dari-nol)
8. [Menjalankan Project Harian](#8-menjalankan-project-harian)
9. [Database dan Flyway](#9-database-dan-flyway)
10. [Endpoint Summary](#10-endpoint-summary)
11. [Response Format](#11-response-format)
12. [Security Contract](#12-security-contract)
13. [Testing](#13-testing)
14. [Docker Commands](#14-docker-commands)
15. [Gradle Commands](#15-gradle-commands)
16. [Git Rules](#16-git-rules)
17. [Troubleshooting](#17-troubleshooting)
18. [Catatan Developer](#18-catatan-developer)
19. [Commit Convention](#19-commit-convention)

---

# 1. Tujuan Backend

Backend Teknisio menangani proses utama aplikasi:

- Autentikasi customer dan technician.
- Manajemen data user dan technician profile.
- Master data kategori alat elektronik atau `deviceCategory`.
- Manajemen keahlian technician berdasarkan device category.
- Pencarian technician oleh customer.
- Pembuatan service request oleh customer.
- Proses accept, reject, start, dan complete request oleh technician.
- Tracking status layanan melalui status history.
- Review technician setelah request selesai.
- Persiapan chat, notifikasi, dan admin untuk fase berikutnya.

Untuk MVP desktop, customer **tidak dipaksa memakai GPS**. Customer mengisi alamat secara manual melalui field:

```text
address
addressDetail
```

---

# 2. Tech Stack

| Komponen | Teknologi |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Build Tool | Gradle Kotlin DSL |
| Database | PostgreSQL 16 |
| Migration | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |
| Token | JWT / JJWT 0.12.6 |
| Validation | Spring Validation / Jakarta Validation |
| Monitoring | Spring Boot Actuator |
| Realtime planned | Spring WebSocket |
| Local database | Docker Compose |
| Boilerplate helper | Lombok |

---

# 3. Arsitektur Backend

Backend memakai pendekatan:

```text
Spring MVC + Layered Architecture
```

Alur request:

```text
Client Desktop / Mobile
        ↓
Controller
        ↓
Service
        ↓
Repository
        ↓
Entity
        ↓
PostgreSQL
```

Tanggung jawab tiap layer:

| Layer | Tanggung Jawab |
|---|---|
| Controller | Menerima request, validasi DTO, mengembalikan response API |
| Service | Business logic, validasi ownership, validasi status, mapping response |
| Repository | Query database menggunakan Spring Data JPA |
| Entity | Mapping tabel database |
| DTO Request | Kontrak input dari frontend |
| DTO Response | Kontrak output ke frontend |
| Security | JWT, authorization, current user |
| Common | Response wrapper, exception, utility |

Catatan penting:

- Controller tidak boleh berisi business logic berat.
- Entity tidak boleh langsung dikirim ke frontend.
- Semua response harus lewat DTO response.
- Semua endpoint resmi menggunakan field berbahasa Inggris.

---

# 4. Struktur Folder

Struktur utama backend:

```text
src/main/java/com/teknisio/
├── TeknisioBackendApplication.java
├── common/
│   ├── exception/
│   ├── response/
│   └── util/
├── config/
├── controllers/
├── dto/
│   ├── requests/
│   └── responses/
├── model/
│   ├── entities/
│   ├── entities/base/
│   └── enums/
├── repositories/
├── security/
├── services/
└── websocket/
```

Struktur resource:

```text
src/main/resources/
├── application.yml
├── db/
│   ├── migration/
│   └── develop/
├── static/
└── templates/
```

Package root aplikasi adalah:

```java
package com.teknisio;
```

Karena `TeknisioBackendApplication.java` berada di `com.teknisio`, Spring Boot otomatis melakukan component scan ke semua sub-package seperti:

```text
com.teknisio.controllers
com.teknisio.services
com.teknisio.repositories
com.teknisio.model
com.teknisio.security
com.teknisio.common
```

---

# 5. Aturan Kontrak API

Aturan kontrak API Teknisio:

- Backend hanya satu untuk semua client.
- Jangan membuat endpoint khusus desktop atau khusus Android.
- Endpoint resmi memakai bahasa Inggris.
- Field request dan response API memakai bahasa Inggris.
- Nama internal Java/database boleh tetap bahasa Indonesia jika sudah ada.
- Jangan expose entity JPA langsung ke response.
- Gunakan DTO request dan DTO response.
- Semua response memakai `ApiResponse<T>`.
- Semua error melewati global exception handler atau security handler.
- Untuk MVP, customer memilih `deviceCategoryIds`, bukan detail `jenis_layanan`.
- Untuk MVP, customer mengisi `issueDescription`, `address`, dan optional `addressDetail`.

Mapping istilah:

| Konsep | Nama API | Nama Internal |
|---|---|---|
| User | `user` | `User`, `users` |
| Customer | `customer` | `User` role `CUSTOMER` |
| Technician | `technician` | `User` role `TECHNICIAN`, `TeknisiProfile` |
| Kategori alat elektronik | `deviceCategory` | `KategoriLayanan` |
| Keahlian technician | `technicianDeviceCategory` | `TeknisiKategoriLayanan` |
| Permintaan layanan | `serviceRequest` | `PermintaanLayanan` |
| Kategori terpilih | `selectedDeviceCategories` | `PermintaanLayananKategori` |
| Deskripsi masalah | `issueDescription` | `deskripsiMasalah` |
| Alamat | `address` | `alamat` |
| Detail alamat | `addressDetail` | `detailAlamat` |
| Riwayat status | `statusHistory` | `RiwayatStatus` |
| Catatan technician | `technicianNote` | `catatanTeknisi` |
| Alasan batal | `cancelReason` | `alasanBatal` |
| Alasan tolak | `rejectReason` | `alasanTolak` |

---

# 6. Environment Variables

File `.env` wajib dibuat di root project, sejajar dengan:

```text
docker-compose.yml
build.gradle.kts
settings.gradle.kts
```

Contoh `.env.example`:

```env
APP_NAME=teknisio-backend

POSTGRES_HOST=localhost
POSTGRES_PORT=5433
POSTGRES_DB=teknisio_db
POSTGRES_USER=teknisio_user
POSTGRES_PASSWORD=change_this_password

SERVER_PORT=8080

JWT_SECRET=change_this_secret_minimum_32_characters
JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=http://localhost:3000
```

Catatan:

- Jangan commit `.env`.
- Commit hanya `.env.example`.
- `JWT_SECRET` untuk development boleh random production-like, tetapi jangan berubah setiap aplikasi start.
- `JWT_EXPIRATION_MS=86400000` berarti access token berlaku 24 jam.
- Port PostgreSQL default Docker project ini adalah `5433` di host dan `5432` di container.

Generate JWT secret random:

```bash
openssl rand -base64 64
```

---

# 7. Menjalankan Project dari Nol

## 7.1 Install tools

Pastikan sudah terinstall:

- Java 17
- Docker
- Docker Compose
- Git
- IDE seperti IntelliJ IDEA atau VS Code

Cek Java:

```bash
java -version
```

Cek Docker:

```bash
docker --version
docker compose version
```

## 7.2 Clone repository

```bash
git clone <url-repository>
cd teknisio_backend
```

Sesuaikan nama folder jika repository berbeda.

## 7.3 Buat `.env`

```bash
cp .env.example .env
```

Lalu isi nilai environment sesuai laptop masing-masing.

## 7.4 Jalankan database

```bash
docker compose up -d
```

Cek container:

```bash
docker ps
```

Container yang diharapkan:

```text
teknisio-postgres
```

## 7.5 Jalankan backend

Linux/macOS:

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```

Backend berjalan di:

```text
http://localhost:8080
```

## 7.6 Cek health

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

---

# 8. Menjalankan Project Harian

Untuk developer yang sudah setup sebelumnya:

```bash
docker compose up -d
./gradlew bootRun
```

Build sebelum commit:

```bash
./gradlew clean build
```

Jalankan strict regression test:

```bash
bash develop/api-smoke-test.sh
```

---

# 9. Database dan Flyway

Project memakai Flyway untuk database migration.

Lokasi migration:

```text
src/main/resources/db/migration
```

Migration yang digunakan:

```text
V1__create_enums.sql
V2__create_core_tables.sql
V3__create_indexes.sql
V4__create_triggers.sql
V5__seed_device_categories.sql
V6__create_reviews.sql
```

Fungsi tiap migration:

| File | Fungsi |
|---|---|
| `V1__create_enums.sql` | Membuat extension `pgcrypto` dan enum PostgreSQL |
| `V2__create_core_tables.sql` | Membuat tabel utama dan relasi FK |
| `V3__create_indexes.sql` | Membuat index untuk query utama |
| `V4__create_triggers.sql` | Membuat trigger `updated_at`, status flow, dan status history |
| `V5__seed_device_categories.sql` | Seed kategori alat elektronik default |
| `V6__create_reviews.sql` | Membuat tabel review |

Aturan penting:

- Jangan edit migration lama yang sudah pernah dijalankan di database tim.
- Untuk perubahan schema baru, buat file migration baru.
- Jangan masukkan `src/main/resources/db/develop` ke lokasi Flyway normal.
- `db/develop` hanya untuk script cleanup/reset development lokal.

Cek history Flyway:

```bash
docker exec -it teknisio-postgres psql -U teknisio_user -d teknisio_db
```

Lalu:

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Cek tabel:

```sql
\dt
```

---

# 10. Endpoint Summary

## 10.1 Public endpoints

```text
POST /api/auth/register/customer
POST /api/auth/register/technician
POST /api/auth/login
GET  /api/device-categories
GET  /api/device-categories/{deviceCategoryId}
GET  /actuator/health
GET  /actuator/info
```

## 10.2 Authenticated endpoint

```text
GET /api/auth/profile
```

## 10.3 Technician device category endpoints

```text
POST   /api/technicians/device-categories
GET    /api/technicians/device-categories
DELETE /api/technicians/device-categories/{deviceCategoryId}
```

## 10.4 Customer technician discovery endpoints

```text
GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}
GET /api/customers/technicians/{technicianProfileId}
```

Optional query:

```text
availabilityStatus=ONLINE|OFFLINE|BUSY|ON_LEAVE
sort=rating|totalJobs|name
```

## 10.5 Customer service request endpoints

```text
POST  /api/customers/service-requests
GET   /api/customers/service-requests
GET   /api/customers/service-requests/{serviceRequestId}
GET   /api/customers/service-requests/{serviceRequestId}/status-history
PATCH /api/customers/service-requests/{serviceRequestId}/cancel
POST  /api/customers/service-requests/{serviceRequestId}/review
```

Catatan:

- `status-history` sudah tersedia di controller/service, tetapi perlu strict smoke test lanjutan.
- `review` sudah tersedia di controller/service, tetapi perlu strict smoke test lanjutan.

## 10.6 Technician service request endpoints

```text
GET   /api/technicians/service-requests
GET   /api/technicians/service-requests/{serviceRequestId}
GET   /api/technicians/service-requests/{serviceRequestId}/status-history
PATCH /api/technicians/service-requests/{serviceRequestId}/accept
PATCH /api/technicians/service-requests/{serviceRequestId}/reject
PATCH /api/technicians/service-requests/{serviceRequestId}/start
PATCH /api/technicians/service-requests/{serviceRequestId}/complete
```

Optional query:

```text
status=WAITING|ACCEPTED|ON_PROGRESS|COMPLETED|CANCELLED|REJECTED
sort=latest|oldest
```

## 10.7 Next endpoints

```text
PUT   /api/users/me
PATCH /api/technicians/availability
PUT   /api/technicians/profile
GET   /api/customers/technicians/{technicianProfileId}/reviews
```

## 10.8 Deferred endpoints

```text
POST  /api/auth/refresh
POST  /api/auth/logout
POST  /api/service-requests/{serviceRequestId}/messages
GET   /api/service-requests/{serviceRequestId}/messages
GET   /api/notifications
PATCH /api/notifications/{notificationId}/read
GET   /api/admin/users
GET   /api/admin/service-requests
```

---

# 11. Response Format

Semua response API memakai wrapper:

```java
ApiResponse<T>
```

## 11.1 Success response

```json
{
  "success": true,
  "message": "Success message",
  "data": {},
  "errors": {}
}
```

## 11.2 Error response

```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "errors": {}
}
```

## 11.3 Validation error

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "email": "Email is required",
    "password": "Password is required"
  }
}
```

Catatan:

- `success=true` hanya untuk HTTP 2xx.
- `success=false` untuk HTTP 4xx dan 5xx.
- `errors` digunakan terutama untuk validation error.

---

# 12. Security Contract

## 12.1 Public

Endpoint berikut tidak butuh token:

```text
POST /api/auth/register/customer
POST /api/auth/register/technician
POST /api/auth/login
GET  /api/device-categories
GET  /api/device-categories/{deviceCategoryId}
GET  /actuator/health
GET  /actuator/info
```

## 12.2 Authenticated

Endpoint berikut butuh token valid:

```text
GET /api/auth/profile
```

## 12.3 Role based

```text
/api/customers/**    -> role CUSTOMER
/api/technicians/**  -> role TECHNICIAN
/api/admin/**        -> role ADMIN
```

Expected error:

| Kondisi | HTTP | Message |
|---|---:|---|
| Tanpa token | 401 | `Unauthorized` |
| Token role salah | 403 | `Forbidden` |
| Token invalid | 401 | `Unauthorized` |

---

# 13. Testing

## 13.1 Unit/integration test Gradle

```bash
./gradlew test
```

## 13.2 Build validation

```bash
./gradlew clean build
```

## 13.3 Health check

```bash
curl http://localhost:8080/actuator/health
```

## 13.4 Strict smoke test

Script utama regression API:

```bash
bash develop/api-smoke-test.sh
```

Target terakhir:

```text
ALL STRICT API SMOKE TESTS V5 PASSED
Passed: 736
Failed: 0
```

Setiap endpoint baru wajib dites minimal:

- Success case.
- Tanpa token jika protected.
- Wrong role jika role-based.
- Invalid UUID jika memakai UUID.
- Not found jika data tidak ada.
- Ownership protection jika data bukan milik user login.
- Validation error jika body tidak valid.
- Invalid enum jika memakai enum.
- Invalid status transition jika update status.
- Response sukses memakai `ApiResponse`.
- Response error memakai `ApiResponse`.

---

# 14. Docker Commands

Menjalankan database:

```bash
docker compose up -d
```

Stop database:

```bash
docker compose down
```

Stop dan hapus data lokal:

```bash
docker compose down -v
```

Melihat container:

```bash
docker ps
```

Melihat log PostgreSQL:

```bash
docker logs teknisio-postgres
```

Masuk PostgreSQL:

```bash
docker exec -it teknisio-postgres psql -U teknisio_user -d teknisio_db
```

Keluar dari psql:

```sql
\q
```

---

# 15. Gradle Commands

Menjalankan backend:

```bash
./gradlew bootRun
```

Build:

```bash
./gradlew build
```

Clean build:

```bash
./gradlew clean build
```

Test:

```bash
./gradlew test
```

Clean:

```bash
./gradlew clean
```

---

# 16. Git Rules

## 16.1 Jangan commit

```text
.env
.env.*
!.env.example
build/
.gradle/
.idea/
.vscode/
*.log
*.session.sql
application-local.yml
application-local.properties
```

## 16.2 Boleh dan perlu commit

```text
.env.example
docker-compose.yml
build.gradle.kts
settings.gradle.kts
src/main/resources/application.yml
src/main/resources/db/migration/*.sql
src/main/java/**
src/test/java/**
develop/api-smoke-test.sh
Readme.md
Roadmap.md
```

Catatan penting:

- Jangan ignore semua `*.sh`, karena `develop/api-smoke-test.sh` perlu di-commit.
- Jangan ignore semua `*.txt`, karena beberapa dokumentasi atau sample bisa saja perlu di-commit.
- Script SQL session lokal seperti `Teknisio Local DB.session.sql` tetap jangan di-commit.

---

# 17. Troubleshooting

## 17.1 Docker permission denied

Error:

```text
permission denied while trying to connect to the Docker daemon socket
```

Solusi:

```bash
sudo usermod -aG docker $USER
```

Lalu logout dan login ulang.

## 17.2 Port PostgreSQL bentrok

Cek proses yang memakai port:

```bash
sudo lsof -i :5432
sudo lsof -i :5433
```

Solusi cepat: ubah `POSTGRES_PORT` di `.env`.

```env
POSTGRES_PORT=5433
```

Lalu restart container:

```bash
docker compose down
docker compose up -d
```

## 17.3 Credential database salah

Error:

```text
password authentication failed for user "teknisio_user"
```

Solusi:

- Pastikan `.env` sama dengan `application.yml`.
- Jika volume lama masih memakai password lama, reset volume lokal:

```bash
docker compose down -v
docker compose up -d
./gradlew bootRun
```

## 17.4 Flyway checksum mismatch

Error:

```text
Validate failed: Migration checksum mismatch
```

Penyebab:

- Migration lama yang sudah pernah dijalankan berubah.

Solusi development lokal:

```bash
docker compose down -v
docker compose up -d
./gradlew bootRun
```

Solusi tim:

- Jangan edit migration lama.
- Buat migration baru.

## 17.5 Hibernate validate error

Error:

```text
Schema-validation: wrong column type encountered
```

Cek:

- Nama tabel.
- Nama kolom.
- Tipe data.
- Enum PostgreSQL.
- `columnDefinition` pada entity enum.
- Migration sudah jalan atau belum.

## 17.6 Aplikasi tidak membaca `.env`

Cek `application.yml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

Pastikan `.env` berada di root project.

## 17.7 API protected return 403 padahal sudah login

Cek role token:

```text
CUSTOMER hanya boleh /api/customers/**
TECHNICIAN hanya boleh /api/technicians/**
ADMIN hanya boleh /api/admin/**
```

---

# 18. Catatan Developer

- Jalankan PostgreSQL sebelum backend.
- Jangan hardcode credential di source code.
- Jangan commit `.env`.
- Semua field API harus English.
- Entity dan tabel internal boleh tetap memakai bahasa Indonesia.
- Jangan expose entity langsung ke response.
- Status history jangan di-insert manual dari Java service; database trigger sudah menangani.
- Jangan pakai `JenisLayanan` untuk flow MVP stable.
- Customer memilih `deviceCategoryIds`, bukan detail servis.
- Setiap fitur baru harus masuk strict smoke test.
- Commit per phase supaya mudah rollback.

---

# 19. Commit Convention

Contoh commit backend phase:

```bash
git add .
git commit -m "phase 5 add technician service request workflow"
```

Contoh commit test:

```bash
git add develop/api-smoke-test.sh
git commit -m "test add strict service request workflow smoke test"
```

Contoh commit dokumentasi:

```bash
git add Readme.md Roadmap.md
git commit -m "docs update backend readme and roadmap"
```
