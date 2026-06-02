# Roadmap Backend Teknisio

Roadmap pengerjaan backend **Teknisio** untuk aplikasi desktop dan mobile.

README ini menjadi panduan utama backend: berisi prinsip pengembangan, flow MVP, status pengerjaan, roadmap per modul, dan kontrak API yang sudah dibuat maupun yang akan dibuat.

---

## Prinsip Utama

- Backend hanya satu untuk semua client.
- Client boleh berbeda: JavaFX Desktop, Android Java, atau client lain.
- Backend tetap melayani semuanya melalui REST API + JSON.
- Data yang keluar/masuk API wajib menggunakan English.
- Nama package, entity, tabel, dan function internal boleh tetap menggunakan bahasa Indonesia jika sudah ada.
- Jangan membuat endpoint khusus Android atau khusus Desktop.
- Jangan expose entity database langsung sebagai response API.
- DTO adalah kontrak antara backend dan frontend.
- Endpoint resmi menggunakan English.
- Endpoint Indonesia seperti `/api/kategori`, `/api/permintaan`, atau `/api/teknisi` tidak dipakai sebagai kontrak resmi.
- Semua response API wajib memakai format `ApiResponse<T>`.
- Semua error wajib konsisten melalui global exception handler atau security handler.
- Untuk MVP, customer tidak memilih detail jenis servis seperti `AC Cleaning`, `AC Repair`, `Refrigerator Freon Refill`, atau sejenisnya.
- Untuk MVP, customer hanya memilih `deviceCategoryIds` dan mengisi `issueDescription`.

---

## Legend Status

| Badge | Arti |
|---|---|
| ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) | Sudah selesai dan sudah dites manual |
| ![ongoing](https://img.shields.io/badge/%5Bongoing%5D-blue?style=flat-square) | Sedang dikerjakan / next immediate |
| ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) | Belum dikerjakan |
| ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) | Ditunda setelah MVP stable |
| ![legacy](https://img.shields.io/badge/%5Blegacy%5D-lightgrey?style=flat-square) | Ada di konsep/schema lama, tapi tidak dipakai untuk MVP stable |

---

## Flow Final MVP

```text
Customer register / login
↓
Customer masuk ke halaman home
↓
Customer melihat daftar alat elektronik:
Air Conditioner, Refrigerator, Washing Machine, Television, Fan, Rice Cooker, Oven, Mixer, dll.
↓
Customer memilih salah satu alat elektronik, misalnya Air Conditioner
↓
Backend mencari technician yang punya keahlian Air Conditioner
↓
Frontend menampilkan daftar technician yang bisa menangani Air Conditioner
↓
Customer bisa filter berdasarkan availabilityStatus dan sort berdasarkan rating/totalJobs/name
↓
Customer memilih salah satu technician
↓
Customer melihat detail technician dan semua supportedDeviceCategories
Contoh: Air Conditioner + Refrigerator
↓
Customer boleh memilih Air Conditioner saja, atau Air Conditioner + Refrigerator
↓
Customer mengisi address, addressDetail, dan issueDescription
↓
Customer membuat service request
↓
Status awal service request = WAITING
↓
Technician melihat request masuk
↓
Technician accept atau reject request
↓
Jika accepted, technician mulai pengerjaan
↓
Technician complete pengerjaan
↓
Status history tercatat
↓
Customer melihat status request
↓
Customer dapat membatalkan request selama status masih WAITING, ACCEPTED, atau ON_PROGRESS
↓
Customer memberi review setelah request completed
```

---

## Istilah Resmi API

| Konsep | Nama API | Nama internal yang boleh tetap dipakai |
|---|---|---|
| User | `user` | `User`, `users` |
| Customer | `customer` | `User` role `CUSTOMER` |
| Technician | `technician` | `User` role `TECHNICIAN`, `TeknisiProfile` |
| Alat elektronik | `deviceCategory` | `KategoriLayanan` |
| Keahlian technician | `technicianDeviceCategory` | `TeknisiKategoriLayanan` |
| Permintaan layanan | `serviceRequest` | `PermintaanLayanan` |
| Kategori yang dipilih dalam order | `selectedDeviceCategories` | `PermintaanLayananKategori` |
| Deskripsi masalah | `issueDescription` | `deskripsiMasalah` |
| Alamat | `address` | `alamat` |
| Detail alamat | `addressDetail` | `detailAlamat` |
| Status request | `status` | `RequestStatus` |
| Riwayat status | `statusHistory` | `RiwayatStatus` |
| Estimasi biaya | `estimatedCost` | `estimasiBiaya` |
| Biaya akhir | `finalCost` | `biayaAkhir` |
| Catatan technician | `technicianNote` | `catatanTeknisi` |
| Alasan batal | `cancelReason` | `alasanBatal` |
| Alasan tolak | `rejectReason` | `alasanTolak` |

Catatan:

- `JenisLayanan` dan `TeknisiLayanan` boleh tetap ada sebagai legacy.
- Untuk MVP baru, flow tidak memakai `jenis_layanan`.
- Jika nanti fitur berkembang, `jenis_layanan` bisa dipakai lagi untuk detail layanan spesifik.

---

# 0. Fondasi Backend

Target: backend punya struktur rapi, response seragam, error handling, repository, security, database migration, dan siap dikembangkan per modul.

---

## BE-00 [MVP] Rapikan struktur package

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `config`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `model`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `model.entities`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `model.entities.base`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `model.enums`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `repositories`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `dto.requests`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `dto.responses`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `services`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `controllers`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `security`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `common.response`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `common.exception`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Package `common.util`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Package `websocket` ditunda sampai fitur realtime/chat/notifikasi

---

## BE-01 [MVP] Buat global response format

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Buat `ApiResponse<T>`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response sukses punya `success`, `message`, `data`, `errors`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response error punya `success`, `message`, `data`, `errors`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Semua controller baru memakai `ApiResponse`

Format response sukses:

```json
{
  "success": true,
  "message": "Success message",
  "data": {},
  "errors": null
}
```

Format response error:

```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "errors": null
}
```

---

## BE-02 [MVP] Buat global exception handler

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Buat `GlobalExceptionHandler`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle validation error
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle bad request
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle unauthorized
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle forbidden
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle not found
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle conflict
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Handle internal server error

---

## BE-03 [MVP] Siapkan DTO validation

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambahkan dependency validation
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@NotBlank`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@NotEmpty`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@Email`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@Size`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Gunakan `@Pattern` jika perlu
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan validasi bisnis kompleks di DTO; validasi bisnis tetap di service

---

## BE-04 [MVP] Buat repository untuk entity inti

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `UserRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `UserSessionRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `TeknisiProfileRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `KategoriLayananRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `TeknisiKategoriLayananRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `PermintaanLayananRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `PermintaanLayananKategoriRepository`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `RiwayatStatusRepository`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) `ReviewRepository` jika tabel review sudah dibuat
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) `NotificationRepository` jika fitur notifikasi dikerjakan
- ![legacy](https://img.shields.io/badge/%5Blegacy%5D-lightgrey?style=flat-square) `JenisLayananRepository` tetap legacy
- ![legacy](https://img.shields.io/badge/%5Blegacy%5D-lightgrey?style=flat-square) `TeknisiLayananRepository` tetap legacy

---

## BE-05 [MVP] Validasi koneksi database dan Flyway

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `./gradlew clean build` sukses
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `./gradlew bootRun` sukses
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tabel terbentuk otomatis lewat Flyway
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `/actuator/health` status `UP`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tidak ada error migration

Migration aktif:

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `V1__create_enums.sql`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `V2__create_core_tables.sql`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `V3__create_indexes.sql`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `V4__create_triggers.sql`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `V5__seed_device_categories.sql`

Catatan:

- Jangan mengubah migration lama kalau sudah pernah dijalankan di database tim.
- Jika ada perubahan schema, buat migration baru `V6__nama_perubahan.sql`.

---

# 1. Auth dan Session

Target: customer dan technician bisa register, login, melihat profile, dan mengakses endpoint sesuai role.

---

## BE-10 [MVP] Register customer

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `POST /api/auth/register/customer`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Public endpoint
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `name` wajib
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `email` wajib dan format email valid
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `phoneNumber` wajib dan unik
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `password` minimal
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan password dalam bentuk hash BCrypt
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Role otomatis `CUSTOMER`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status akun otomatis `ACTIVE`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response field menggunakan English

Contract:

```http
POST /api/auth/register/customer
Content-Type: application/json
```

Request:

```json
{
  "name": "Customer Demo",
  "email": "customer.demo@mail.com",
  "phoneNumber": "+6281234567890",
  "password": "password123",
  "address": "Jl. Contoh No. 123"
}
```

Success `201`:

```json
{
  "success": true,
  "message": "Customer registered successfully",
  "data": {
    "accessToken": "jwt-token",
    "tokenType": "Bearer",
    "user": {
      "userId": "uuid",
      "name": "Customer Demo",
      "email": "customer.demo@mail.com",
      "phoneNumber": "+6281234567890",
      "role": "CUSTOMER"
    }
  },
  "errors": null
}
```

---

## BE-11 [MVP] Register technician

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `POST /api/auth/register/technician`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Public endpoint
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan data user dengan role `TECHNICIAN`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Buat otomatis data `teknisi_profile`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status ketersediaan default `OFFLINE`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Rating default `0`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Rating count default `0`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Total pekerjaan default `0`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response field menggunakan English

Contract:

```http
POST /api/auth/register/technician
Content-Type: application/json
```

Request:

```json
{
  "name": "Technician Demo",
  "email": "technician.demo@mail.com",
  "phoneNumber": "+6281234567891",
  "password": "password123",
  "address": "Jl. Teknisi No. 1",
  "description": "Teknisi elektronik rumah tangga"
}
```

Success `201`:

```json
{
  "success": true,
  "message": "Technician registered successfully",
  "data": {
    "accessToken": "jwt-token",
    "tokenType": "Bearer",
    "user": {
      "userId": "uuid",
      "name": "Technician Demo",
      "email": "technician.demo@mail.com",
      "phoneNumber": "+6281234567891",
      "role": "TECHNICIAN"
    }
  },
  "errors": null
}
```

---

## BE-12 [MVP] Login

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `POST /api/auth/login`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Public endpoint
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Login pakai email dan password
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Cek password dengan BCrypt
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Cek status akun `ACTIVE`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Generate JWT access token
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return token dan data user

Contract:

```http
POST /api/auth/login
Content-Type: application/json
```

Request:

```json
{
  "email": "customer.demo@mail.com",
  "password": "password123"
}
```

Success `200`:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "jwt-token",
    "tokenType": "Bearer",
    "user": {
      "userId": "uuid",
      "name": "Customer Demo",
      "email": "customer.demo@mail.com",
      "phoneNumber": "+6281234567890",
      "role": "CUSTOMER"
    }
  },
  "errors": null
}
```

---

## BE-13 [MVP] Lihat profil sendiri

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/auth/profile`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Harus login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Ambil user dari JWT token
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tidak expose password hash
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response field menggunakan English

Contract:

```http
GET /api/auth/profile
Authorization: Bearer {token}
```

Success `200`:

```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "userId": "uuid",
    "name": "Customer Demo",
    "email": "customer.demo@mail.com",
    "phoneNumber": "+6281234567890",
    "profilePhoto": null,
    "address": "Jl. Contoh No. 123",
    "role": "CUSTOMER",
    "accountStatus": "ACTIVE"
  },
  "errors": null
}
```

---

## BE-14 [NEXT] Refresh token

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `POST /api/auth/refresh`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Validasi refresh token
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Validasi session belum expired
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Validasi session belum revoked
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Generate access token baru

Catatan:

- Tabel `user_session` sudah tersedia, tapi flow refresh token belum prioritas MVP stable awal.

---

## BE-15 [NEXT] Logout server-side

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `POST /api/auth/logout`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Revoke session aktif
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Isi `revoked_at`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Token lama tidak bisa dipakai refresh

Catatan:

- Untuk MVP awal, logout bisa dilakukan di client dengan menghapus token.

---

## BE-16 [MVP] Role-based access

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint `/api/customers/**` hanya bisa diakses `CUSTOMER`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint `/api/technicians/**` hanya bisa diakses `TECHNICIAN`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint `/api/admin/**` hanya bisa diakses `ADMIN`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint auth register/login public
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint `GET /api/device-categories/**` public
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint profile wajib login

---

# 2. Master Data Device Category dan Technician Skill

Target: customer bisa melihat daftar alat elektronik dan technician bisa mengatur keahlian alat elektronik yang dikuasai.

---

## BE-20 [MVP] Seed data kategori alat elektronik

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Air Conditioner`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Refrigerator`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Washing Machine`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Television`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Fan`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Rice Cooker`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Oven`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tambah kategori `Mixer`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Pastikan seed data tidak duplikat saat migration dijalankan ulang
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Pastikan semua data default `aktif = true`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tidak perlu seed jenis layanan/detail servis untuk MVP

---

## BE-21 [MVP] List kategori alat elektronik aktif

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/device-categories`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint boleh diakses tanpa login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya tampilkan data `aktif = true`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan tampilkan data yang sudah soft delete
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response menggunakan field English
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response berisi `deviceCategoryId`, `name`, `icon`

Contract:

```http
GET /api/device-categories
```

Success `200`:

```json
{
  "success": true,
  "message": "Device categories retrieved successfully",
  "data": [
    {
      "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
      "name": "Air Conditioner",
      "icon": "air-conditioner"
    }
  ],
  "errors": null
}
```

---

## BE-22 [MVP] Detail kategori alat elektronik

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/device-categories/{deviceCategoryId}`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint boleh diakses tanpa login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori ditemukan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori aktif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori belum soft delete
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid, return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika kategori tidak ditemukan, return `404`

Contract:

```http
GET /api/device-categories/{deviceCategoryId}
```

Success `200`:

```json
{
  "success": true,
  "message": "Device category retrieved successfully",
  "data": {
    "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
    "name": "Air Conditioner",
    "icon": "air-conditioner"
  },
  "errors": null
}
```

---

## BE-23 [MVP] Technician tambah keahlian alat elektronik

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `POST /api/technicians/device-categories`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician yang boleh akses
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request berisi `deviceCategoryId`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori ditemukan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori aktif dan belum soft delete
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Cegah duplikasi skill aktif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Skill yang pernah dihapus bisa diaktifkan ulang

Contract:

```http
POST /api/technicians/device-categories
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d"
}
```

Success `201`:

```json
{
  "success": true,
  "message": "Technician device category added successfully",
  "data": {
    "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
    "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
    "name": "Air Conditioner",
    "icon": "air-conditioner",
    "active": true
  },
  "errors": null
}
```

---

## BE-24 [MVP] Technician lihat keahlian sendiri

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/technicians/device-categories`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician yang boleh akses
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return semua keahlian aktif milik technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan tampilkan kategori yang nonaktif atau soft delete
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response menggunakan field English

Contract:

```http
GET /api/technicians/device-categories
Authorization: Bearer {technicianToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Technician device categories retrieved successfully",
  "data": [
    {
      "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
      "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
      "name": "Air Conditioner",
      "icon": "air-conditioner",
      "active": true
    }
  ],
  "errors": null
}
```

---

## BE-25 [MVP] Technician hapus/nonaktifkan keahlian alat elektronik

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `DELETE /api/technicians/device-categories/{deviceCategoryId}`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician yang boleh akses
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi relasi milik technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hapus memakai soft-disable `aktif = false`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Relasi tidak hilang dari database

Contract:

```http
DELETE /api/technicians/device-categories/{deviceCategoryId}
Authorization: Bearer {technicianToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Technician device category removed successfully",
  "data": null,
  "errors": null
}
```

---

# 3. Customer Technician Discovery

Target: customer bisa mencari technician berdasarkan device category, filter/sort technician, dan melihat detail technician sebelum membuat service request.

---

## BE-30 [MVP] Search technician berdasarkan kategori alat elektronik

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint hanya untuk customer yang sudah login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Token technician return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `deviceCategoryId` wajib
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori ditemukan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi kategori aktif dan belum soft delete
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tampilkan technician yang memiliki keahlian pada kategori tersebut
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya tampilkan technician dengan relasi skill aktif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya tampilkan akun technician aktif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response menggunakan field English

Contract:

```http
GET /api/customers/technicians?deviceCategoryId=6e6349a8-e528-4a38-8b1a-6123c4f1c40d
Authorization: Bearer {customerToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Technicians retrieved successfully",
  "data": [
    {
      "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
      "name": "Technician Demo",
      "profilePhoto": null,
      "availabilityStatus": "OFFLINE",
      "averageRating": 0.00,
      "ratingCount": 0,
      "totalJobs": 0,
      "description": "Teknisi elektronik rumah tangga",
      "supportedDeviceCategories": [
        {
          "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
          "name": "Air Conditioner",
          "icon": "air-conditioner"
        }
      ]
    }
  ],
  "errors": null
}
```

---

## BE-31 [MVP] Filter technician berdasarkan availability status

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}&availabilityStatus=ONLINE`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Filter bersifat optional
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi invalid `availabilityStatus`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Error message menjelaskan allowed values

Allowed values:

```text
ONLINE
OFFLINE
BUSY
ON_LEAVE
```

Invalid response `400`:

```json
{
  "success": false,
  "message": "Invalid availabilityStatus. Allowed values: ONLINE, OFFLINE, BUSY, ON_LEAVE",
  "data": null,
  "errors": null
}
```

---

## BE-32 [MVP] Sort technician

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}&sort=rating`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort bersifat optional
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort berdasarkan rating
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort berdasarkan total pekerjaan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort berdasarkan nama
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi invalid `sort`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Sort berdasarkan harga jika harga technician sudah tersedia
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Sort berdasarkan jarak jika latitude/longitude technician sudah tersedia

Allowed values:

```text
rating
totalJobs
name
```

Default sort:

```text
averageRating DESC
then totalJobs DESC
then name ASC
```

Invalid response `400`:

```json
{
  "success": false,
  "message": "Invalid sort. Allowed values: rating, totalJobs, name",
  "data": null,
  "errors": null
}
```

---

## BE-33 [MVP] Detail technician untuk customer

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/customers/technicians/{technicianProfileId}`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint hanya untuk customer yang sudah login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Token technician return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi technician ditemukan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi akun technician aktif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi role user `TECHNICIAN`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `supportedDeviceCategories`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan tampilkan kategori yang tidak aktif atau sudah soft delete

Contract:

```http
GET /api/customers/technicians/b15f2a79-3082-49f0-91e8-3c3f9037a2ba
Authorization: Bearer {customerToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Technician retrieved successfully",
  "data": {
    "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
    "name": "Technician Demo",
    "profilePhoto": null,
    "availabilityStatus": "OFFLINE",
    "averageRating": 0.00,
    "ratingCount": 0,
    "totalJobs": 0,
    "description": "Teknisi elektronik rumah tangga",
    "supportedDeviceCategories": [
      {
        "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
        "name": "Air Conditioner",
        "icon": "air-conditioner"
      }
    ]
  },
  "errors": null
}
```

---

# 4. Service Request — Customer

Target: customer bisa membuat, melihat, dan membatalkan service request. Customer memilih technician terlebih dahulu, lalu memilih satu atau lebih device category yang dikuasai technician tersebut.

---

## BE-40 [MVP] Customer membuat service request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `POST /api/customers/service-requests`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint hanya untuk customer yang sudah login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Token technician return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `technicianProfileId` wajib
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `technicianProfileId` harus UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi technician ditemukan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi akun technician aktif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi role technician `TECHNICIAN`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `deviceCategoryIds` minimal 1
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `deviceCategoryIds` maksimal 10 item
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi semua device category UUID valid
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi tidak ada duplicate category
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi semua device category ditemukan, aktif, dan belum soft delete
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi technician memiliki semua skill sesuai `deviceCategoryIds`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `issueDescription` wajib
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `address` wajib
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `addressDetail` opsional
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan ke `permintaan_layanan`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan selected categories ke `permintaan_layanan_kategori`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status awal `WAITING`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `kode_permintaan` digenerate database
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `riwayat_status` dibuat otomatis oleh trigger database

Contract:

```http
POST /api/customers/service-requests
Authorization: Bearer {customerToken}
Content-Type: application/json
```

Request:

```json
{
  "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
  "deviceCategoryIds": [
    "6e6349a8-e528-4a38-8b1a-6123c4f1c40d"
  ],
  "issueDescription": "AC tidak dingin dan mengeluarkan suara berisik",
  "address": "Jl. Contoh No. 123, Medan",
  "addressDetail": "Rumah warna putih pagar hitam"
}
```

Success `201`:

```json
{
  "success": true,
  "message": "Service request created successfully",
  "data": {
    "serviceRequestId": "52df9e18-79f4-47f2-9513-37f169bb680c",
    "serviceRequestCode": "REQ-20260530-4EACD1E9",
    "customerId": "437cb551-365e-4939-903f-ce9511a38a63",
    "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
    "status": "WAITING",
    "issueDescription": "AC tidak dingin dan mengeluarkan suara berisik",
    "address": "Jl. Contoh No. 123, Medan",
    "addressDetail": "Rumah warna putih pagar hitam",
    "cancelReason": null,
    "selectedDeviceCategories": [
      {
        "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
        "name": "Air Conditioner",
        "icon": "air-conditioner"
      }
    ],
    "requestTime": "2026-05-30T02:53:04.917315284+07:00",
    "cancelledAt": null
  },
  "errors": null
}
```

Important error cases:

```json
{
  "success": false,
  "message": "Invalid technician profile id",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Technician not found",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Device category ids must not contain duplicate values",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Technician does not support selected device category: Refrigerator",
  "data": null,
  "errors": null
}
```

---

## BE-41 [MVP] Customer lihat riwayat service request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/customers/service-requests`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint hanya untuk customer login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Technician token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya tampilkan request milik customer login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Filter `status` opsional
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi invalid `status`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Default sort terbaru berdasarkan `waktuPermintaan DESC`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sertakan selected device categories
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sertakan `cancelReason` dan `cancelledAt`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jangan tampilkan request milik customer lain
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Query param `sort=latest|oldest` eksplisit belum diprioritaskan karena default latest sudah cukup untuk MVP awal
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Technician summary detail seperti `technicianName` dapat ditambahkan nanti jika UI membutuhkan

Contract:

```http
GET /api/customers/service-requests?status=WAITING
Authorization: Bearer {customerToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service requests retrieved successfully",
  "data": [
    {
      "serviceRequestId": "52df9e18-79f4-47f2-9513-37f169bb680c",
      "serviceRequestCode": "REQ-20260530-4EACD1E9",
      "customerId": "437cb551-365e-4939-903f-ce9511a38a63",
      "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
      "status": "WAITING",
      "issueDescription": "AC tidak dingin dan mengeluarkan suara berisik",
      "address": "Jl. Contoh No. 123, Medan",
      "addressDetail": "Rumah warna putih pagar hitam",
      "cancelReason": null,
      "selectedDeviceCategories": [
        {
          "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
          "name": "Air Conditioner",
          "icon": "air-conditioner"
        }
      ],
      "requestTime": "2026-05-30T02:53:04.917315284+07:00",
      "cancelledAt": null
    }
  ],
  "errors": null
}
```

---

## BE-42 [MVP] Customer lihat detail service request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/customers/service-requests/{serviceRequestId}`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint hanya untuk customer login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Technician token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik customer login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return detail service request
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return selected device categories
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `cancelReason` dan `cancelledAt`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Technician summary detail seperti `technicianName` dapat ditambahkan nanti jika UI membutuhkan
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Status history detail dipisah ke BE-62

Contract:

```http
GET /api/customers/service-requests/52df9e18-79f4-47f2-9513-37f169bb680c
Authorization: Bearer {customerToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request retrieved successfully",
  "data": {
    "serviceRequestId": "52df9e18-79f4-47f2-9513-37f169bb680c",
    "serviceRequestCode": "REQ-20260530-4EACD1E9",
    "customerId": "437cb551-365e-4939-903f-ce9511a38a63",
    "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
    "status": "WAITING",
    "issueDescription": "AC tidak dingin dan mengeluarkan suara berisik",
    "address": "Jl. Contoh No. 123, Medan",
    "addressDetail": "Rumah warna putih pagar hitam",
    "cancelReason": null,
    "selectedDeviceCategories": [
      {
        "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
        "name": "Air Conditioner",
        "icon": "air-conditioner"
      }
    ],
    "requestTime": "2026-05-30T02:53:04.917315284+07:00",
    "cancelledAt": null
  },
  "errors": null
}
```

---

## BE-43 [MVP] Customer batalkan service request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `PATCH /api/customers/service-requests/{serviceRequestId}/cancel`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint hanya untuk customer login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Technician token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik customer login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya customer pemilik request
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya boleh jika status `WAITING`, `ACCEPTED`, atau `ON_PROGRESS`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tidak boleh cancel status final `COMPLETED`, `CANCELLED`, `REJECTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan `cancelReason`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `cancelReason` wajib dan maksimal 1000 karakter
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Update status menjadi `CANCELLED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `cancelledAt`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `diubahOlehTerakhir`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis membuat status history
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh`

Contract:

```http
PATCH /api/customers/service-requests/{serviceRequestId}/cancel
Authorization: Bearer {customerToken}
Content-Type: application/json
```

Request:

```json
{
  "cancelReason": "Saya ingin membatalkan permintaan"
}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request cancelled successfully",
  "data": {
    "serviceRequestId": "52df9e18-79f4-47f2-9513-37f169bb680c",
    "serviceRequestCode": "REQ-20260530-4EACD1E9",
    "customerId": "437cb551-365e-4939-903f-ce9511a38a63",
    "technicianProfileId": "b15f2a79-3082-49f0-91e8-3c3f9037a2ba",
    "status": "CANCELLED",
    "issueDescription": "AC tidak dingin dan mengeluarkan suara berisik",
    "address": "Jl. Contoh No. 123, Medan",
    "addressDetail": "Rumah warna putih pagar hitam",
    "cancelReason": "Saya ingin membatalkan permintaan",
    "selectedDeviceCategories": [
      {
        "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
        "name": "Air Conditioner",
        "icon": "air-conditioner"
      }
    ],
    "requestTime": "2026-05-30T02:53:04.917315284+07:00",
    "cancelledAt": "2026-05-31T14:27:10.123456+07:00"
  },
  "errors": null
}
```

Important error cases:

```json
{
  "success": false,
  "message": "Service request cannot be cancelled from status CANCELLED",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Invalid service request id",
  "data": null,
  "errors": null
}
```

---

# 5. Service Request — Technician

Target: technician bisa melihat request masuk yang memang ditujukan kepadanya, lalu menerima, menolak, memulai, dan menyelesaikan layanan.

Catatan:

- Karena customer sudah memilih technician sebelum membuat request, technician tidak mengambil request bebas dari kategori.
- Request masuk adalah request yang `technicianProfileId`-nya sama dengan technician login.
- Semua endpoint pada modul ini hanya untuk role `TECHNICIAN`.
- Semua perubahan status mengikuti flow database: `WAITING → ACCEPTED / REJECTED / CANCELLED`, `ACCEPTED → ON_PROGRESS / CANCELLED`, dan `ON_PROGRESS → COMPLETED / CANCELLED`.
- Timestamp status seperti `acceptedAt`, `startedAt`, `completedAt`, `cancelledAt`, dan `rejectedAt` diisi otomatis oleh trigger database.
- Status history juga dibuat otomatis oleh trigger database; Java service tidak insert `RiwayatStatus` manual.

---

## BE-50 [MVP] Technician lihat request masuk / request miliknya

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/technicians/service-requests`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Ambil `TeknisiProfile` dari user login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tampilkan request untuk technician tersebut
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Bisa filter status `WAITING`, `ACCEPTED`, `ON_PROGRESS`, `COMPLETED`, `CANCELLED`, `REJECTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status filter menerima lowercase dan dinormalisasi ke enum
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Invalid status return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort `latest` atau `oldest`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Invalid sort return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Default sort `latest`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sertakan customer summary: `customerId`, `customerName`, `customerPhoneNumber`, `customerProfilePhoto`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sertakan selected device categories
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sertakan status timestamp dan alasan cancel/reject
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh` V5

Contract:

```http
GET /api/technicians/service-requests?status=WAITING&sort=latest
Authorization: Bearer {technicianToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service requests retrieved successfully",
  "data": [
    {
      "serviceRequestId": "uuid",
      "serviceRequestCode": "REQ-20260531-XXXXXXXX",
      "customerId": "uuid",
      "customerName": "Customer Demo",
      "customerPhoneNumber": "+6281234567890",
      "customerProfilePhoto": null,
      "technicianProfileId": "uuid",
      "status": "WAITING",
      "issueDescription": "AC tidak dingin",
      "address": "Jl. Contoh No. 123",
      "addressDetail": "Rumah pagar hitam",
      "estimatedCost": null,
      "finalCost": null,
      "technicianNote": null,
      "cancelReason": null,
      "rejectReason": null,
      "selectedDeviceCategories": [
        {
          "deviceCategoryId": "6e6349a8-e528-4a38-8b1a-6123c4f1c40d",
          "name": "Air Conditioner",
          "icon": "air-conditioner"
        }
      ],
      "requestTime": "2026-05-31T08:00:00Z",
      "acceptedAt": null,
      "startedAt": null,
      "completedAt": null,
      "cancelledAt": null,
      "rejectedAt": null
    }
  ],
  "errors": null
}
```

---

## BE-51 [MVP] Technician lihat detail request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `GET /api/technicians/service-requests/{serviceRequestId}`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `serviceRequestId` harus UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik technician login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi technician adalah technician yang dipilih pada request
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return data customer
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return lokasi
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `selectedDeviceCategories`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `issueDescription`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return status
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `estimatedCost`, `finalCost`, dan `technicianNote`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Return `cancelReason`, `rejectReason`, dan semua timestamp status
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Status history detail dipisah ke BE-62
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh` V5

Contract:

```http
GET /api/technicians/service-requests/{serviceRequestId}
Authorization: Bearer {technicianToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request retrieved successfully",
  "data": {
    "serviceRequestId": "uuid",
    "serviceRequestCode": "REQ-20260531-XXXXXXXX",
    "customerId": "uuid",
    "customerName": "Customer Demo",
    "customerPhoneNumber": "+6281234567890",
    "customerProfilePhoto": null,
    "technicianProfileId": "uuid",
    "status": "WAITING",
    "issueDescription": "AC tidak dingin",
    "address": "Jl. Contoh No. 123",
    "addressDetail": "Rumah pagar hitam",
    "estimatedCost": null,
    "finalCost": null,
    "technicianNote": null,
    "cancelReason": null,
    "rejectReason": null,
    "selectedDeviceCategories": [],
    "requestTime": "2026-05-31T08:00:00Z",
    "acceptedAt": null,
    "startedAt": null,
    "completedAt": null,
    "cancelledAt": null,
    "rejectedAt": null
  },
  "errors": null
}
```

---

## BE-52 [MVP] Technician accept request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/accept`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `serviceRequestId` harus UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik technician login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request harus `WAITING`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan `WAITING`, return `409`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request harus ditujukan ke technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi technician masih memiliki semua skill untuk selectedDeviceCategories
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika skill tidak aktif / sudah dihapus, return `409`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Set status `ACCEPTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `diubahOlehTerakhir`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis isi `waktuDiterima` / `acceptedAt`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis insert status history
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh` V5

Contract:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/accept
Authorization: Bearer {technicianToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request accepted successfully",
  "data": {
    "serviceRequestId": "uuid",
    "serviceRequestCode": "REQ-20260531-XXXXXXXX",
    "technicianProfileId": "uuid",
    "status": "ACCEPTED",
    "acceptedAt": "2026-05-31T08:00:00Z",
    "startedAt": null,
    "completedAt": null,
    "cancelledAt": null,
    "rejectedAt": null
  },
  "errors": null
}
```

Important error cases:

```json
{
  "success": false,
  "message": "Service request cannot be accepted from status ACCEPTED",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Technician does not support selected device category: Air Conditioner",
  "data": null,
  "errors": null
}
```

---

## BE-53 [MVP] Technician reject request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/reject`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `serviceRequestId` harus UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik technician login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request harus `WAITING`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan `WAITING`, return `409`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request harus ditujukan ke technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan `rejectReason` jika dikirim
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `rejectReason` maksimal 1000 karakter
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status menjadi `REJECTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `diubahOlehTerakhir`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis isi `waktuDitolak` / `rejectedAt`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis insert status history
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh` V5

Contract:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/reject
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "rejectReason": "Jadwal teknisi penuh"
}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request rejected successfully",
  "data": {
    "serviceRequestId": "uuid",
    "serviceRequestCode": "REQ-20260531-XXXXXXXX",
    "technicianProfileId": "uuid",
    "status": "REJECTED",
    "rejectReason": "Jadwal teknisi penuh",
    "acceptedAt": null,
    "startedAt": null,
    "completedAt": null,
    "cancelledAt": null,
    "rejectedAt": "2026-05-31T08:00:00Z"
  },
  "errors": null
}
```

Important error cases:

```json
{
  "success": false,
  "message": "Service request cannot be rejected from status REJECTED",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "rejectReason": "Reject reason must be at most 1000 characters"
  }
}
```

---

## BE-54 [MVP] Technician mulai pengerjaan

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/start`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `serviceRequestId` harus UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik technician login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request harus `ACCEPTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request masih `WAITING`, sudah `COMPLETED`, `CANCELLED`, atau `REJECTED`, return `409`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician yang dipilih pada request
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status menjadi `ON_PROGRESS`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `diubahOlehTerakhir`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis isi `waktuDiproses` / `startedAt`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis insert status history
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh` V5

Contract:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/start
Authorization: Bearer {technicianToken}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request started successfully",
  "data": {
    "serviceRequestId": "uuid",
    "serviceRequestCode": "REQ-20260531-XXXXXXXX",
    "technicianProfileId": "uuid",
    "status": "ON_PROGRESS",
    "estimatedCost": null,
    "finalCost": null,
    "technicianNote": null,
    "acceptedAt": "2026-05-31T08:00:00Z",
    "startedAt": "2026-05-31T08:05:00Z",
    "completedAt": null,
    "cancelledAt": null,
    "rejectedAt": null
  },
  "errors": null
}
```

Important error cases:

```json
{
  "success": false,
  "message": "Service request cannot be started from status WAITING",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Service request cannot be started from status COMPLETED",
  "data": null,
  "errors": null
}
```

---

## BE-55 [MVP] Technician selesaikan pengerjaan

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `PATCH /api/technicians/service-requests/{serviceRequestId}/complete`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician login
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer token return `403`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Tanpa token return `401`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `serviceRequestId` harus UUID
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika UUID invalid return `400`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request tidak ditemukan return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request bukan milik technician login return `404`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Request harus `ON_PROGRESS`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Jika request masih `WAITING`, baru `ACCEPTED`, atau sudah final, return `409`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Hanya technician yang dipilih pada request
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status menjadi `COMPLETED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan `finalCost`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `finalCost` wajib dikirim
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `finalCost` tidak boleh negatif
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Simpan `technicianNote` jika dikirim
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi `technicianNote` maksimal 1000 karakter
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Isi `diubahOlehTerakhir`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis isi `waktuSelesai` / `completedAt`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) DB trigger otomatis insert status history
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Increment `totalJobs` technician ditunda sampai aturan statistik/rating final
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah masuk strict regression test `develop/api-smoke-test.sh` V5

Contract:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/complete
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "finalCost": 150000,
  "technicianNote": "AC sudah dibersihkan dan freon dicek"
}
```

Success `200`:

```json
{
  "success": true,
  "message": "Service request completed successfully",
  "data": {
    "serviceRequestId": "uuid",
    "serviceRequestCode": "REQ-20260531-XXXXXXXX",
    "technicianProfileId": "uuid",
    "status": "COMPLETED",
    "estimatedCost": null,
    "finalCost": 150000.00,
    "technicianNote": "AC sudah dibersihkan dan freon dicek",
    "acceptedAt": "2026-05-31T08:00:00Z",
    "startedAt": "2026-05-31T08:05:00Z",
    "completedAt": "2026-05-31T08:30:00Z",
    "cancelledAt": null,
    "rejectedAt": null
  },
  "errors": null
}
```

Important error cases:

```json
{
  "success": false,
  "message": "Service request cannot be completed from status ACCEPTED",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Invalid request body",
  "data": null,
  "errors": null
}
```

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "finalCost": "Final cost is required"
  }
}
```

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "technicianNote": "Technician note must be at most 1000 characters"
  }
}
```

---

# 6. Status dan Riwayat Status

Target: semua perubahan status service request tercatat dan bisa dilihat sebagai timeline.

---

## BE-60 [MVP] Trigger status flow service request

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Initial status harus `WAITING`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `WAITING` boleh ke `ACCEPTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `WAITING` boleh ke `REJECTED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `WAITING` boleh ke `CANCELLED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `ACCEPTED` boleh ke `ON_PROGRESS`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `ACCEPTED` boleh ke `CANCELLED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `ON_PROGRESS` boleh ke `COMPLETED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `ON_PROGRESS` boleh ke `CANCELLED`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Status final tidak bisa diubah lagi

Final statuses:

```text
COMPLETED
CANCELLED
REJECTED
```

---

## BE-61 [MVP] Trigger status history otomatis

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Saat insert service request, otomatis insert row ke `riwayat_status`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Saat status berubah, otomatis insert row ke `riwayat_status`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `status_sebelum` tersimpan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `status_sesudah` tersimpan
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) `diubah_oleh` tersimpan dari `diubah_oleh_terakhir`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sudah dites: create request menghasilkan `WAITING` history

Catatan penting:

- Jangan insert `RiwayatStatus` manual dari Java saat create/update status.
- Database trigger sudah menangani status history.

---

## BE-62 [NEXT] API lihat timeline status request

Target next immediate setelah BE-55 selesai. Database trigger status history sudah aktif, sehingga modul ini fokus expose read API.

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint customer: `GET /api/customers/service-requests/{serviceRequestId}/status-history`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint technician: `GET /api/technicians/service-requests/{serviceRequestId}/status-history`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Customer hanya bisa melihat history request miliknya
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Technician hanya bisa melihat history request yang ditujukan kepadanya
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Sort by `createdAt ASC`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response field English

Planned response item:

```json
{
  "statusHistoryId": "uuid",
  "previousStatus": null,
  "newStatus": "WAITING",
  "note": "Service request created",
  "changedByUserId": "uuid",
  "changedAt": "timestamp"
}
```

---

# 7. Profil User dan Technician

Target: customer dan technician bisa melihat serta mengubah profil dasar. Technician juga bisa mengatur availability status.

---

## BE-70 [MVP] Update profil sendiri

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Endpoint: `PUT /api/users/me`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Bisa update `name`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Bisa update `phoneNumber`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Bisa update `profilePhoto`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Bisa update `address`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Validasi phone number jika berubah
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Response menggunakan field English

Planned contract:

```http
PUT /api/users/me
Authorization: Bearer {token}
Content-Type: application/json
```

Request:

```json
{
  "name": "Nama Baru",
  "phoneNumber": "+6281234567890",
  "profilePhoto": "https://example.com/photo.jpg",
  "address": "Alamat baru"
}
```

---

## BE-71 [NEXT] Technician update availability status

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Endpoint: `PATCH /api/technicians/availability`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Hanya technician yang boleh akses
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Customer token return `403`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Tanpa token return `401`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Validasi invalid status
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Update `status_ketersediaan` di `teknisi_profile`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Response menggunakan field English

Allowed values:

```text
ONLINE
OFFLINE
BUSY
ON_LEAVE
```

Planned contract:

```http
PATCH /api/technicians/availability
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "availabilityStatus": "ONLINE"
}
```

---

## BE-72 [NEXT] Technician update deskripsi profil

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Endpoint: `PUT /api/technicians/profile`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Bisa update `description`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Bisa update profile photo lewat data user
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Response menggunakan field English

---

# 8. Review

Target: customer bisa memberi rating setelah service request selesai.

Catatan:

- Jika tabel review belum ada di schema final, buat migration baru terlebih dahulu.
- Review boleh masuk MVP setelah flow request customer-technician selesai.

---

## BE-80 [MVP] Review schema

- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Buat tabel `review`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Relasi ke `permintaan_layanan`
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Relasi ke customer
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Relasi ke technician profile
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Rating 1 sampai 5
- ![finished](https://img.shields.io/badge/%5Bfinished%5D-brightgreen?style=flat-square) Satu request hanya boleh satu review

Candidate columns:

```text
id_review
id_permintaan
id_customer
id_teknisi_profile
rating
comment
created_at
updated_at
deleted_at
```

---

## BE-81 [MVP] Customer membuat review

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Endpoint: `POST /api/customers/service-requests/{serviceRequestId}/review`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Request harus milik customer login
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Request harus `COMPLETED`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Rating wajib 1 sampai 5
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Comment opsional
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Satu request hanya boleh satu review
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Update `ratingAvg` technician
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Update `ratingCount` technician

Planned contract:

```http
POST /api/customers/service-requests/{serviceRequestId}/review
Authorization: Bearer {customerToken}
Content-Type: application/json
```

Request:

```json
{
  "rating": 5,
  "comment": "Teknisi ramah dan pengerjaan cepat"
}
```

---

## BE-82 [NEXT] Lihat review technician

- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Endpoint: `GET /api/customers/technicians/{technicianProfileId}/reviews`
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Return daftar review technician
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Bisa pagination
- ![todo](https://img.shields.io/badge/%5Btodo%5D-lightgrey?style=flat-square) Response menggunakan field English

---

# 9. Chat REST dan WebSocket

Target: customer dan technician terkait service request bisa berkomunikasi. Untuk MVP awal, fitur ini bisa ditunda sampai flow service request stabil.

---

## BE-90 [LATER] Kirim pesan text via REST

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `POST /api/service-requests/{serviceRequestId}/messages`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Validasi user bagian dari request
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Pesan text wajib punya `message`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Message type `TEXT`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Simpan sender
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Simpan `sentAt`

---

## BE-91 [LATER] Ambil riwayat chat

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `GET /api/service-requests/{serviceRequestId}/messages`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Validasi user bagian dari request
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Urutkan dari pesan lama ke baru
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Bisa pagination

---

## BE-92 [LATER] WebSocket chat real-time

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint WS: `/ws`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Buat room berdasarkan `serviceRequestId`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Broadcast pesan baru ke customer dan technician terkait

---

# 10. Notifikasi

Target: user bisa melihat notifikasi perubahan status dan pesan baru. Untuk MVP awal, fitur ini bisa ditunda dan frontend dapat melakukan refresh manual.

---

## BE-100 [LATER] List notifikasi user

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `GET /api/notifications`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Hanya notifikasi milik user login
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Urutkan terbaru
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Bisa pagination

---

## BE-101 [LATER] Tandai notifikasi dibaca

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `PATCH /api/notifications/{notificationId}/read`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Validasi notifikasi milik user
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Isi `readAt`

---

## BE-102 [LATER] Buat notifikasi saat status berubah

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Saat customer membuat request, technician dapat notifikasi
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Saat technician menerima request, customer dapat notifikasi
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Saat technician menolak request, customer dapat notifikasi
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Saat technician mulai kerja, customer dapat notifikasi
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Saat technician menyelesaikan request, customer dapat notifikasi
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Saat customer membatalkan request, technician dapat notifikasi

---

# 11. Admin Opsional

Catatan: role `ADMIN` ada di backend, tapi fokus utama MVP stable adalah customer dan technician. Admin dikerjakan setelah flow utama stabil.

---

## BE-110 [LATER] Admin CRUD device category

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `GET /api/admin/device-categories`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `POST /api/admin/device-categories`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `PATCH /api/admin/device-categories/{deviceCategoryId}`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `DELETE /api/admin/device-categories/{deviceCategoryId}`

---

## BE-111 [LATER] Admin lihat semua user

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `GET /api/admin/users`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter role
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter accountStatus
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Search name/email

---

## BE-112 [LATER] Admin lihat semua service request

- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Endpoint: `GET /api/admin/service-requests`
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter status
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter tanggal
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter technician
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter customer
- ![deferred](https://img.shields.io/badge/%5Bdeferred%5D-lightgrey?style=flat-square) Filter device category

---

# 12. Ringkasan Contract API

## 12.1 Endpoint yang sudah dibuat

### Auth

```text
POST /api/auth/register/customer
POST /api/auth/register/technician
POST /api/auth/login
GET  /api/auth/profile
```

### Device Category

```text
GET /api/device-categories
GET /api/device-categories/{deviceCategoryId}
```

### Technician Device Category

```text
GET    /api/technicians/device-categories
POST   /api/technicians/device-categories
DELETE /api/technicians/device-categories/{deviceCategoryId}
```

### Customer Technician Discovery

```text
GET /api/customers/technicians
GET /api/customers/technicians/{technicianProfileId}
```

### Customer Service Request

```text
POST  /api/customers/service-requests
GET   /api/customers/service-requests
GET   /api/customers/service-requests/{serviceRequestId}
PATCH /api/customers/service-requests/{serviceRequestId}/cancel
```

### Technician Service Request

```text
GET   /api/technicians/service-requests
GET   /api/technicians/service-requests/{serviceRequestId}
PATCH /api/technicians/service-requests/{serviceRequestId}/accept
PATCH /api/technicians/service-requests/{serviceRequestId}/reject
PATCH /api/technicians/service-requests/{serviceRequestId}/start
PATCH /api/technicians/service-requests/{serviceRequestId}/complete
```

---

## 12.2 Endpoint yang akan dibuat berikutnya

### Customer Service Request

```text
GET   /api/customers/service-requests/{serviceRequestId}/status-history
POST  /api/customers/service-requests/{serviceRequestId}/review
```

### Technician Service Request

```text
GET   /api/technicians/service-requests/{serviceRequestId}/status-history
```

### User / Technician Profile

```text
PUT   /api/users/me
PATCH /api/technicians/availability
PUT   /api/technicians/profile
```

### Review

```text
GET /api/customers/technicians/{technicianProfileId}/reviews
```

### Deferred

```text
POST  /api/auth/refresh
POST  /api/auth/logout
GET   /api/notifications
PATCH /api/notifications/{notificationId}/read
GET   /api/admin/users
GET   /api/admin/service-requests
```

---

# 13. Security Contract

## Public

```text
POST /api/auth/register/customer
POST /api/auth/register/technician
POST /api/auth/login
GET  /api/device-categories
GET  /api/device-categories/{deviceCategoryId}
GET  /actuator/health
GET  /actuator/info
```

## Authenticated

```text
GET /api/auth/profile
```

## CUSTOMER only

```text
/api/customers/**
```

## TECHNICIAN only

```text
/api/technicians/**
```

## ADMIN only

```text
/api/admin/**
```

---

# 14. Testing Checklist per Endpoint

Setiap endpoint baru wajib dites minimal:

- [ ] Success case
- [ ] Tanpa token jika endpoint protected
- [ ] Wrong role jika endpoint role-based
- [ ] Invalid UUID jika endpoint memakai path/query UUID
- [ ] Not found jika data tidak ada
- [ ] Forbidden ownership jika data bukan milik user login
- [ ] Validation error jika request body tidak valid
- [ ] Invalid enum jika memakai enum
- [ ] Invalid status transition jika endpoint update status
- [ ] Response sukses memakai `ApiResponse`
- [ ] Response error memakai `ApiResponse`
- [ ] Tambahkan ke strict regression test `develop/api-smoke-test.sh`

Strict regression test saat ini:

```bash
bash develop/api-smoke-test.sh
```

Target terakhir yang sudah tercapai:

```text
ALL STRICT API SMOKE TESTS V5 PASSED
Passed: 736
Failed: 0
```

---

# 15. Urutan Pengerjaan Terdekat

Status terakhir yang sudah selesai dan sudah masuk strict regression test:

```text
✅ BE-41 Customer List Service Requests
✅ BE-42 Customer Detail Service Request
✅ BE-43 Customer Cancel Service Request
✅ BE-50 Technician List Service Requests
✅ BE-51 Technician Detail Service Request
✅ BE-52 Technician Accept Request
✅ BE-53 Technician Reject Request
✅ BE-54 Technician Start Work
✅ BE-55 Technician Complete Work
```

Urutan paling aman dari posisi sekarang:

```text
1. BE-62 Status History Read API
2. BE-71 Technician Availability
3. BE-80 Review Schema
4. BE-81 Create Review
5. BE-82 List Review / Technician Rating Read API
6. BE-90 Chat Schema
7. BE-91 Chat History
8. BE-92 WebSocket Chat
```

Catatan:

- Backend core service request technician sudah lengkap sampai status `COMPLETED`.
- Jangan masuk review sebelum BE-62 selesai, karena review idealnya bergantung pada request yang sudah `COMPLETED` dan histori status yang jelas.
- Jangan masuk mobile/desktop integrasi penuh sebelum endpoint status history minimal tersedia.
- Setelah setiap endpoint baru, update `develop/api-smoke-test.sh`.

---

# 16. Catatan Penting untuk Tim

- Jangan pakai `JenisLayanan` untuk flow MVP stable.
- Jangan buat endpoint khusus desktop atau khusus Android.
- Jangan expose entity JPA langsung ke response.
- Jangan insert status history manual saat create/update status request, karena database trigger sudah menangani.
- Semua API field harus English.
- Semua response harus memakai `ApiResponse<T>`.
- Semua protected endpoint harus dites `401` dan `403`.
- Commit per phase supaya mudah rollback.
- Jangan commit file lokal seperti `.env`, `.idea`, `.vscode`, `bin`, `build`, atau file SQL session lokal.

---

# 17. Commit Convention

Contoh commit per phase:

```bash
git add .
git commit -m "phase 3f add customer service request creation api"
```

Contoh commit dokumentasi:

```bash
git add Readme.md
git commit -m "docs update backend roadmap and api contract"
```
