# Roadmap Backend Teknisio — Updated

Roadmap pengerjaan backend **Teknisio** untuk aplikasi desktop dan mobile.

Dokumen ini menjadi panduan utama backend: prinsip pengembangan, flow MVP, status pengerjaan, kontrak API, prioritas sprint, dan aturan testing. Roadmap ini sudah disesuaikan dengan kondisi backend terbaru: flow auth, device category, technician skill, technician discovery, service request customer, service request technician, status history read API, update profile, dan create review sudah mulai terbentuk.

---

## Prinsip Utama

- Backend hanya satu untuk semua client.
- Client boleh berbeda: JavaFX Desktop, Android Java, atau client lain.
- Backend tetap melayani semuanya melalui REST API + JSON.
- Data yang keluar/masuk API wajib menggunakan field **English**.
- Nama package, entity, tabel, column, dan function internal boleh tetap bahasa Indonesia jika sudah terlanjur stabil.
- Jangan membuat endpoint khusus Android atau khusus Desktop.
- Jangan expose entity JPA langsung sebagai response API.
- DTO adalah kontrak antara backend dan frontend.
- Endpoint resmi menggunakan English.
- Endpoint Indonesia seperti `/api/kategori`, `/api/permintaan`, atau `/api/teknisi` tidak dipakai sebagai kontrak resmi.
- Semua response API wajib memakai `ApiResponse<T>`.
- Semua error wajib konsisten melalui global exception handler atau security handler.
- Untuk MVP, customer tidak memilih detail jenis servis seperti `AC Cleaning`, `AC Repair`, `Refrigerator Freon Refill`, dan sejenisnya.
- Untuk MVP, customer memilih satu atau lebih `deviceCategoryIds`, memilih technician, lalu mengisi `issueDescription`, `address`, dan `addressDetail`.
- Karena arah client berubah ke desktop, lokasi boleh input manual melalui `address` dan `addressDetail`; GPS tidak wajib untuk MVP desktop.
- Chat real-time dan notification tetap disimpan sebagai later/deferred sampai core service request + review stabil.

---

## Legend Status

| Status | Arti |
|---|---|
| `[finished]` | Sudah selesai dan sudah masuk manual/strict regression test |
| `[implemented]` | Kode/endpoint sudah ada, tetapi belum dikunci penuh di strict smoke test |
| `[ongoing]` | Sedang dikerjakan / next immediate |
| `[todo]` | Belum dikerjakan |
| `[deferred]` | Ditunda setelah MVP stable |
| `[legacy]` | Ada di konsep/schema lama, tetapi tidak dipakai untuk flow MVP stable |

Aturan status:

- Jangan tandai `[finished]` hanya karena kode compile.
- Endpoint baru minimal `[implemented]` dulu.
- Naik menjadi `[finished]` hanya jika sudah lolos test manual penting dan masuk `develop/api-smoke-test.sh` atau strict regression test yang aktif.
- Untuk fitur yang sengaja ditunda, pakai `[deferred]`, bukan `[todo]`.

---

## Snapshot Status Backend Terbaru

```text
[finished]    Backend foundation
[finished]    Global response format
[finished]    Global exception handling
[finished]    JWT access token authentication
[finished]    Register customer
[finished]    Register technician
[finished]    Login
[finished]    Profile current user
[finished]    Role-based access CUSTOMER / TECHNICIAN / ADMIN
[finished]    Device category list/detail
[finished]    Technician device category skill add/list/remove
[finished]    Customer technician discovery
[finished]    Customer service request create/list/detail/cancel
[finished]    Technician service request list/detail/accept/reject/start/complete
[implemented] Status history read API for customer and technician
[finished]    Update profile sendiri basic
[finished]    Review schema V6
[implemented] Customer create review
[todo]         Technician update availability
[todo]         Technician update profile/description
[todo]         List review technician
[deferred]    Refresh token
[deferred]    Logout server-side
[deferred]    Chat REST/WebSocket
[deferred]    Notification
[deferred]    Admin module
```

Catatan penting:

- Auth terbaru memakai **access token JWT**.
- `POST /api/auth/refresh` dan `POST /api/auth/logout` **tidak boleh dianggap selesai** pada roadmap ini jika tidak ada di controller/service branch terbaru.
- Status history dan create review sudah ada di controller/service, tetapi tetap perlu dikunci ke strict regression test sebelum statusnya dinaikkan menjadi `[finished]`.
- `ReviewRepository` dan migration `V6__create_reviews.sql` sudah ada, jadi roadmap review tidak lagi dimulai dari nol.
- `.gitignore` saat ini perlu dicek karena ignore `*.sh` dapat membuat script smoke test tidak ikut commit.

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
Status history WAITING otomatis tercatat oleh trigger database
↓
Technician melihat request masuk
↓
Technician accept atau reject request
↓
Jika accepted, technician mulai pengerjaan
↓
Technician complete pengerjaan
↓
Status history tercatat pada setiap perubahan status
↓
Customer melihat status request dan timeline status history
↓
Customer dapat membatalkan request selama status masih WAITING, ACCEPTED, atau ON_PROGRESS
↓
Customer memberi review setelah request COMPLETED
↓
Rating technician diperbarui
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
| Kategori terpilih dalam request | `selectedDeviceCategories` | `PermintaanLayananKategori` |
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
| Rating | `rating` | `rating` |
| Komentar review | `comment` | `comment` |

Catatan:

- `JenisLayanan` dan `TeknisiLayanan` lama boleh dianggap legacy.
- Untuk MVP baru, flow utama tidak memakai `jenis_layanan`.
- Jika fitur berkembang, detail jenis layanan bisa dihidupkan lagi sebagai fitur harga/paket servis.

---

# 0. Fondasi Backend

Target: backend punya struktur rapi, response seragam, error handling, repository, security, database migration, dan siap dikembangkan per modul.

## BE-00 [MVP] Rapikan struktur package

Status: `[finished]`

Checklist:

- `[finished]` Package `config`
- `[finished]` Package `model`
- `[finished]` Package `model.entities`
- `[finished]` Package `model.entities.base`
- `[finished]` Package `model.enums`
- `[finished]` Package `repositories`
- `[finished]` Package `dto.requests`
- `[finished]` Package `dto.responses`
- `[finished]` Package `services`
- `[finished]` Package `controllers`
- `[finished]` Package `security`
- `[finished]` Package `common.response`
- `[finished]` Package `common.exception`
- `[finished]` Package `common.util`
- `[deferred]` Package `websocket` sampai fitur realtime/chat/notifikasi dikerjakan

Struktur standar:

```text
src/main/java/com/teknisio/
├── TeknisioBackendApplication.java
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
├── services/
├── security/
├── common/
│   ├── exception/
│   ├── response/
│   └── util/
└── websocket/         # deferred
```

## BE-01 [MVP] Buat global response format

Status: `[finished]`

Checklist:

- `[finished]` Buat `ApiResponse<T>`
- `[finished]` Response sukses punya `success`, `message`, `data`, `errors`
- `[finished]` Response error punya `success`, `message`, `data`, `errors`
- `[finished]` Semua controller baru memakai `ApiResponse`

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

Catatan:

- Untuk validation error, `errors` berisi object field error.
- Untuk error biasa, `errors` boleh `null` atau `{}` sesuai implementasi yang sudah stabil, tetapi harus konsisten.

## BE-02 [MVP] Buat global exception handler

Status: `[finished]`

Checklist:

- `[finished]` `GlobalExceptionHandler`
- `[finished]` `BadRequestException`
- `[finished]` `UnauthorizedException`
- `[finished]` `ForbiddenException`
- `[finished]` `ResourceNotFoundException`
- `[finished]` `ConflictException`
- `[finished]` Handle validation error
- `[finished]` Handle bad request
- `[finished]` Handle unauthorized
- `[finished]` Handle forbidden
- `[finished]` Handle not found
- `[finished]` Handle conflict
- `[finished]` Handle internal server error

## BE-03 [MVP] Siapkan DTO validation

Status: `[finished]`

Checklist:

- `[finished]` Dependency validation tersedia
- `[finished]` Gunakan `@NotBlank`
- `[finished]` Gunakan `@NotEmpty`
- `[finished]` Gunakan `@Email`
- `[finished]` Gunakan `@Size`
- `[finished]` Gunakan `@Pattern` jika perlu
- `[finished]` Validasi bisnis kompleks tetap di service

Catatan:

- DTO hanya untuk validasi bentuk input.
- Validasi seperti ownership, status transition, duplicate review, dan technician skill harus di service/database.

## BE-04 [MVP] Repository entity inti

Status: `[finished]`

Checklist:

- `[finished]` `UserRepository`
- `[finished]` `UserSessionRepository`
- `[finished]` `TeknisiProfileRepository`
- `[finished]` `KategoriLayananRepository`
- `[finished]` `TeknisiKategoriLayananRepository`
- `[finished]` `PermintaanLayananRepository`
- `[finished]` `PermintaanLayananKategoriRepository`
- `[finished]` `RiwayatStatusRepository`
- `[finished]` `ReviewRepository`
- `[legacy]` `JenisLayananRepository` tidak dipakai flow MVP baru
- `[legacy]` `TeknisiLayananRepository` tidak dipakai flow MVP baru
- `[deferred]` `NotificationRepository` dibuat saat fitur notification dikerjakan

## BE-05 [MVP] Validasi database dan Flyway

Status: `[finished]`

Checklist:

- `[finished]` `./gradlew clean build` sukses
- `[finished]` `./gradlew bootRun` sukses
- `[finished]` Tabel terbentuk otomatis lewat Flyway
- `[finished]` `/actuator/health` status `UP`
- `[finished]` Tidak ada error migration

Migration aktif:

```text
V1__create_enums.sql
V2__create_core_tables.sql
V3__create_indexes.sql
V4__create_triggers.sql
V5__seed_device_categories.sql
V6__create_reviews.sql
```

Aturan migration:

- Jangan edit migration lama yang sudah dijalankan di database tim.
- Perubahan schema baru harus pakai migration baru, misalnya `V7__add_chat_tables.sql`.
- Data seed yang sudah live harus idempotent.

---

# 1. Auth dan Session

Target: customer dan technician bisa register, login, melihat profile, update profile dasar, dan mengakses endpoint sesuai role.

## BE-10 [MVP] Register customer

Status: `[finished]`

Endpoint:

```http
POST /api/auth/register/customer
```

Checklist:

- `[finished]` Public endpoint
- `[finished]` Validasi `name` wajib
- `[finished]` Validasi `email` wajib dan format valid
- `[finished]` Validasi `phoneNumber` wajib dan unik
- `[finished]` Validasi `password` minimal
- `[finished]` Validasi `address` wajib untuk MVP desktop
- `[finished]` Simpan password dengan BCrypt
- `[finished]` Role otomatis `CUSTOMER`
- `[finished]` Status akun otomatis `ACTIVE`
- `[finished]` Return access token dan data user
- `[finished]` Response field menggunakan English

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
    "expiresInMs": 86400000,
    "user": {
      "userId": "uuid",
      "technicianProfileId": null,
      "name": "Customer Demo",
      "email": "customer.demo@mail.com",
      "phoneNumber": "+6281234567890",
      "profilePhoto": null,
      "address": "Jl. Contoh No. 123",
      "role": "CUSTOMER",
      "accountStatus": "ACTIVE"
    }
  },
  "errors": null
}
```

## BE-11 [MVP] Register technician

Status: `[finished]`

Endpoint:

```http
POST /api/auth/register/technician
```

Checklist:

- `[finished]` Public endpoint
- `[finished]` Simpan data user role `TECHNICIAN`
- `[finished]` Buat otomatis `teknisi_profile`
- `[finished]` Status ketersediaan default `OFFLINE`
- `[finished]` Rating default `0`
- `[finished]` Rating count default `0`
- `[finished]` Total pekerjaan default `0`
- `[finished]` Response field menggunakan English

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
    "expiresInMs": 86400000,
    "user": {
      "userId": "uuid",
      "technicianProfileId": "uuid",
      "name": "Technician Demo",
      "email": "technician.demo@mail.com",
      "phoneNumber": "+6281234567891",
      "profilePhoto": null,
      "address": "Jl. Teknisi No. 1",
      "role": "TECHNICIAN",
      "accountStatus": "ACTIVE"
    }
  },
  "errors": null
}
```

## BE-12 [MVP] Login

Status: `[finished]`

Endpoint:

```http
POST /api/auth/login
```

Checklist:

- `[finished]` Public endpoint
- `[finished]` Login pakai email dan password
- `[finished]` Cek password dengan BCrypt
- `[finished]` Cek status akun `ACTIVE`
- `[finished]` Generate JWT access token
- `[finished]` Return token dan data user

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
    "expiresInMs": 86400000,
    "user": {
      "userId": "uuid",
      "technicianProfileId": null,
      "name": "Customer Demo",
      "email": "customer.demo@mail.com",
      "phoneNumber": "+6281234567890",
      "profilePhoto": null,
      "address": "Jl. Contoh No. 123",
      "role": "CUSTOMER",
      "accountStatus": "ACTIVE"
    }
  },
  "errors": null
}
```

## BE-13 [MVP] Lihat profil sendiri

Status: `[finished]`

Endpoint:

```http
GET /api/auth/profile
Authorization: Bearer {token}
```

Checklist:

- `[finished]` Harus login
- `[finished]` Ambil user dari JWT token
- `[finished]` Tidak expose password hash
- `[finished]` Technician profile id tampil untuk technician
- `[finished]` Response field menggunakan English

## BE-14 [DEFERRED] Refresh token

Status: `[deferred]`

Endpoint planned:

```http
POST /api/auth/refresh
```

Checklist planned:

- `[deferred]` Generate refresh token saat login
- `[deferred]` Simpan refresh token hash di `user_session`
- `[deferred]` Validasi refresh token
- `[deferred]` Validasi session belum expired
- `[deferred]` Validasi session belum revoked
- `[deferred]` Generate access token baru

Catatan:

- Branch terbaru belum memiliki endpoint refresh di `AuthController`.
- Untuk MVP desktop awal, access token saja masih boleh.
- Session table boleh tetap ada untuk pengembangan berikutnya.

## BE-15 [DEFERRED] Logout server-side

Status: `[deferred]`

Endpoint planned:

```http
POST /api/auth/logout
```

Checklist planned:

- `[deferred]` Revoke session aktif
- `[deferred]` Isi `revoked_at`
- `[deferred]` Refresh token lama tidak bisa dipakai
- `[deferred]` Optional: access token blacklist jika benar-benar diperlukan

Catatan:

- Untuk MVP awal, logout cukup dilakukan di client dengan menghapus token.
- Server-side logout dikerjakan setelah core MVP stabil.

## BE-16 [MVP] Role-based access

Status: `[finished]`

Checklist:

- `[finished]` Endpoint `/api/customers/**` hanya role `CUSTOMER`
- `[finished]` Endpoint `/api/technicians/**` hanya role `TECHNICIAN`
- `[finished]` Endpoint `/api/admin/**` hanya role `ADMIN`
- `[finished]` Register/login public
- `[finished]` Device category GET public
- `[finished]` Profile wajib login
- `[finished]` PUT `/api/users/me` wajib login
- `[finished]` Security handler return `ApiResponse` untuk `401` dan `403`

---

# 2. Master Data Device Category dan Technician Skill

Target: customer bisa melihat daftar alat elektronik dan technician bisa mengatur keahlian alat elektronik yang dikuasai.

## BE-20 [MVP] Seed data kategori alat elektronik

Status: `[finished]`

Checklist:

- `[finished]` `Air Conditioner`
- `[finished]` `Refrigerator`
- `[finished]` `Washing Machine`
- `[finished]` `Television`
- `[finished]` `Fan`
- `[finished]` `Rice Cooker`
- `[finished]` `Oven`
- `[finished]` `Mixer`
- `[finished]` Seed tidak duplikat saat migration dijalankan ulang
- `[finished]` Default `aktif = true`
- `[finished]` Tidak seed detail jenis layanan untuk MVP

## BE-21 [MVP] List kategori alat elektronik aktif

Status: `[finished]`

Endpoint:

```http
GET /api/device-categories
```

Checklist:

- `[finished]` Public endpoint
- `[finished]` Hanya data aktif
- `[finished]` Tidak tampilkan soft deleted
- `[finished]` Response field English
- `[finished]` Response berisi `deviceCategoryId`, `name`, `icon`

## BE-22 [MVP] Detail kategori alat elektronik

Status: `[finished]`

Endpoint:

```http
GET /api/device-categories/{deviceCategoryId}
```

Checklist:

- `[finished]` Public endpoint
- `[finished]` Validasi UUID
- `[finished]` Validasi kategori ditemukan
- `[finished]` Validasi kategori aktif
- `[finished]` Validasi belum soft delete
- `[finished]` Invalid UUID return `400`
- `[finished]` Not found return `404`

## BE-23 [MVP] Technician tambah keahlian alat elektronik

Status: `[finished]`

Endpoint:

```http
POST /api/technicians/device-categories
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "deviceCategoryId": "uuid"
}
```

Checklist:

- `[finished]` Hanya technician
- `[finished]` Customer token return `403`
- `[finished]` Tanpa token return `401`
- `[finished]` Request berisi `deviceCategoryId`
- `[finished]` Validasi UUID
- `[finished]` Validasi kategori aktif dan belum soft delete
- `[finished]` Cegah duplikasi skill aktif
- `[finished]` Skill yang pernah dihapus bisa diaktifkan ulang

## BE-24 [MVP] Technician lihat keahlian sendiri

Status: `[finished]`

Endpoint:

```http
GET /api/technicians/device-categories
Authorization: Bearer {technicianToken}
```

Checklist:

- `[finished]` Hanya technician
- `[finished]` Return semua skill aktif milik technician login
- `[finished]` Tidak tampilkan kategori nonaktif atau soft deleted
- `[finished]` Response field English

## BE-25 [MVP] Technician hapus/nonaktifkan keahlian alat elektronik

Status: `[finished]`

Endpoint:

```http
DELETE /api/technicians/device-categories/{deviceCategoryId}
Authorization: Bearer {technicianToken}
```

Checklist:

- `[finished]` Hanya technician
- `[finished]` Validasi UUID
- `[finished]` Validasi relasi milik technician login
- `[finished]` Soft-disable `aktif = false`
- `[finished]` Relasi tidak hilang dari database

---

# 3. Customer Technician Discovery

Target: customer bisa mencari technician berdasarkan device category, filter/sort technician, dan melihat detail technician sebelum membuat service request.

## BE-30 [MVP] Search technician berdasarkan kategori alat elektronik

Status: `[finished]`

Endpoint:

```http
GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}
Authorization: Bearer {customerToken}
```

Checklist:

- `[finished]` Hanya customer login
- `[finished]` Tanpa token return `401`
- `[finished]` Technician token return `403`
- `[finished]` Validasi `deviceCategoryId` wajib
- `[finished]` Validasi UUID
- `[finished]` Validasi kategori aktif dan belum soft delete
- `[finished]` Tampilkan technician yang memiliki skill aktif pada kategori tersebut
- `[finished]` Hanya tampilkan akun technician aktif
- `[finished]` Response field English

## BE-31 [MVP] Filter technician berdasarkan availability status

Status: `[finished]`

Endpoint:

```http
GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}&availabilityStatus=ONLINE
```

Checklist:

- `[finished]` Filter optional
- `[finished]` Validasi invalid `availabilityStatus`
- `[finished]` Error message menjelaskan allowed values

Allowed values:

```text
ONLINE
OFFLINE
BUSY
ON_LEAVE
```

## BE-32 [MVP] Sort technician

Status: `[finished]`

Endpoint:

```http
GET /api/customers/technicians?deviceCategoryId={deviceCategoryId}&sort=rating
```

Checklist:

- `[finished]` Sort optional
- `[finished]` Sort by rating
- `[finished]` Sort by total jobs
- `[finished]` Sort by name
- `[finished]` Validasi invalid `sort`
- `[deferred]` Sort by harga jika harga technician tersedia
- `[deferred]` Sort by jarak jika latitude/longitude dipakai lagi

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

## BE-33 [MVP] Detail technician untuk customer

Status: `[finished]`

Endpoint:

```http
GET /api/customers/technicians/{technicianProfileId}
Authorization: Bearer {customerToken}
```

Checklist:

- `[finished]` Hanya customer login
- `[finished]` Tanpa token return `401`
- `[finished]` Technician token return `403`
- `[finished]` Validasi UUID
- `[finished]` Validasi technician ditemukan
- `[finished]` Validasi akun technician aktif
- `[finished]` Validasi role user `TECHNICIAN`
- `[finished]` Return `supportedDeviceCategories`
- `[finished]` Tidak tampilkan kategori nonaktif atau soft deleted

---

# 4. Service Request — Customer

Target: customer bisa membuat, melihat, membaca status history, memberi review, dan membatalkan service request.

## BE-40 [MVP] Customer membuat service request

Status: `[finished]`

Endpoint:

```http
POST /api/customers/service-requests
Authorization: Bearer {customerToken}
Content-Type: application/json
```

Request:

```json
{
  "technicianProfileId": "uuid",
  "deviceCategoryIds": ["uuid"],
  "issueDescription": "AC tidak dingin dan mengeluarkan suara berisik",
  "address": "Jl. Contoh No. 123, Medan",
  "addressDetail": "Rumah warna putih pagar hitam"
}
```

Checklist:

- `[finished]` Hanya customer login
- `[finished]` Tanpa token return `401`
- `[finished]` Technician token return `403`
- `[finished]` Validasi `technicianProfileId` wajib
- `[finished]` Validasi technician profile UUID
- `[finished]` Validasi technician ditemukan dan aktif
- `[finished]` Validasi role technician `TECHNICIAN`
- `[finished]` Validasi `deviceCategoryIds` minimal 1
- `[finished]` Validasi `deviceCategoryIds` maksimal 10 item
- `[finished]` Validasi semua UUID valid
- `[finished]` Validasi tidak ada duplicate category
- `[finished]` Validasi semua kategori aktif dan belum soft delete
- `[finished]` Validasi technician memiliki semua selected skill
- `[finished]` Validasi `issueDescription` wajib
- `[finished]` Validasi `address` wajib
- `[finished]` `addressDetail` opsional
- `[finished]` Simpan ke `permintaan_layanan`
- `[finished]` Simpan selected categories ke `permintaan_layanan_kategori`
- `[finished]` Status awal `WAITING`
- `[finished]` `kode_permintaan` digenerate database
- `[finished]` `riwayat_status` dibuat otomatis oleh trigger database

## BE-41 [MVP] Customer lihat riwayat service request

Status: `[finished]`

Endpoint:

```http
GET /api/customers/service-requests?status=WAITING
Authorization: Bearer {customerToken}
```

Checklist:

- `[finished]` Hanya customer login
- `[finished]` Technician token return `403`
- `[finished]` Tanpa token return `401`
- `[finished]` Hanya tampilkan request milik customer login
- `[finished]` Filter `status` optional
- `[finished]` Validasi invalid `status`
- `[finished]` Default sort `waktuPermintaan DESC`
- `[finished]` Sertakan selected device categories
- `[finished]` Sertakan `cancelReason` dan `cancelledAt`
- `[finished]` Jangan tampilkan request milik customer lain
- `[finished]` Sudah masuk strict regression test V5

## BE-42 [MVP] Customer lihat detail service request

Status: `[finished]`

Endpoint:

```http
GET /api/customers/service-requests/{serviceRequestId}
Authorization: Bearer {customerToken}
```

Checklist:

- `[finished]` Hanya customer login
- `[finished]` Technician token return `403`
- `[finished]` Tanpa token return `401`
- `[finished]` Validasi UUID
- `[finished]` Invalid UUID return `400`
- `[finished]` Not found return `404`
- `[finished]` Request bukan milik customer login return `404`
- `[finished]` Return detail service request
- `[finished]` Return selected device categories
- `[finished]` Return `cancelReason` dan `cancelledAt`
- `[finished]` Sudah masuk strict regression test V5

## BE-43 [MVP] Customer batalkan service request

Status: `[finished]`

Endpoint:

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

Checklist:

- `[finished]` Hanya customer login
- `[finished]` Technician token return `403`
- `[finished]` Tanpa token return `401`
- `[finished]` Validasi UUID
- `[finished]` Request bukan milik customer login return `404`
- `[finished]` Hanya boleh status `WAITING`, `ACCEPTED`, `ON_PROGRESS`
- `[finished]` Tidak boleh cancel status final `COMPLETED`, `CANCELLED`, `REJECTED`
- `[finished]` Simpan `cancelReason`
- `[finished]` Validasi `cancelReason` wajib dan maksimal 1000 karakter
- `[finished]` Update status menjadi `CANCELLED`
- `[finished]` Isi `cancelledAt`
- `[finished]` Isi `diubahOlehTerakhir`
- `[finished]` DB trigger otomatis membuat status history
- `[finished]` Sudah masuk strict regression test V5

## BE-44 [MVP] Customer lihat status history service request

Status: `[implemented]`

Endpoint:

```http
GET /api/customers/service-requests/{serviceRequestId}/status-history
Authorization: Bearer {customerToken}
```

Checklist:

- `[implemented]` Endpoint sudah ada
- `[implemented]` Hanya customer login
- `[implemented]` Customer hanya bisa melihat history request miliknya
- `[implemented]` Validasi UUID
- `[implemented]` Request bukan milik customer login return `404`
- `[implemented]` Sort by `createdAt ASC`
- `[implemented]` Response field English
- `[ongoing]` Tambahkan ke strict regression test

Response item:

```json
{
  "statusHistoryId": "uuid",
  "previousStatus": null,
  "newStatus": "WAITING",
  "note": "Status permintaan dibuat",
  "changedByUserId": "uuid",
  "changedAt": "2026-06-02T10:00:00+07:00"
}
```

## BE-45 [MVP] Customer membuat review

Status: `[implemented]`

Endpoint:

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

Checklist:

- `[implemented]` Endpoint sudah ada
- `[implemented]` Request harus milik customer login
- `[implemented]` Request harus `COMPLETED`
- `[implemented]` Rating wajib 1 sampai 5
- `[implemented]` Comment opsional
- `[implemented]` Satu request hanya boleh satu review
- `[implemented]` Update `ratingAvg` technician
- `[implemented]` Update `ratingCount` technician
- `[ongoing]` Tambahkan ke strict regression test

---

# 5. Service Request — Technician

Target: technician bisa melihat request miliknya, membaca status history, menerima, menolak, memulai, dan menyelesaikan layanan.

Catatan:

- Customer sudah memilih technician sebelum membuat request.
- Request masuk adalah request yang `technicianProfileId`-nya sama dengan technician login.
- Semua endpoint pada modul ini hanya untuk role `TECHNICIAN`.
- Java service tidak insert `RiwayatStatus` manual; database trigger yang menangani.

## BE-50 [MVP] Technician lihat request masuk / request miliknya

Status: `[finished]`

Endpoint:

```http
GET /api/technicians/service-requests?status=WAITING&sort=latest
Authorization: Bearer {technicianToken}
```

Checklist:

- `[finished]` Hanya technician login
- `[finished]` Customer token return `403`
- `[finished]` Tanpa token return `401`
- `[finished]` Ambil `TeknisiProfile` dari user login
- `[finished]` Tampilkan request untuk technician tersebut
- `[finished]` Bisa filter status `WAITING`, `ACCEPTED`, `ON_PROGRESS`, `COMPLETED`, `CANCELLED`, `REJECTED`
- `[finished]` Status filter menerima lowercase dan dinormalisasi
- `[finished]` Invalid status return `400`
- `[finished]` Sort `latest` atau `oldest`
- `[finished]` Invalid sort return `400`
- `[finished]` Default sort `latest`
- `[finished]` Sertakan customer summary
- `[finished]` Sertakan selected device categories
- `[finished]` Sertakan timestamp status dan alasan cancel/reject
- `[finished]` Sudah masuk strict regression test V5

## BE-51 [MVP] Technician lihat detail request

Status: `[finished]`

Endpoint:

```http
GET /api/technicians/service-requests/{serviceRequestId}
Authorization: Bearer {technicianToken}
```

Checklist:

- `[finished]` Hanya technician login
- `[finished]` Customer token return `403`
- `[finished]` Tanpa token return `401`
- `[finished]` Validasi UUID
- `[finished]` Request harus milik technician login
- `[finished]` Request milik technician lain return `404`
- `[finished]` Return customer summary
- `[finished]` Return selected device categories
- `[finished]` Return status timestamp
- `[finished]` Sudah masuk strict regression test V5

## BE-52 [MVP] Technician accept request

Status: `[finished]`

Endpoint:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/accept
Authorization: Bearer {technicianToken}
```

Checklist:

- `[finished]` Hanya technician login
- `[finished]` Request harus milik technician login
- `[finished]` Hanya boleh dari status `WAITING`
- `[finished]` Validasi technician masih mendukung selected categories
- `[finished]` Update status menjadi `ACCEPTED`
- `[finished]` Isi `diubahOlehTerakhir`
- `[finished]` DB trigger isi `acceptedAt`
- `[finished]` DB trigger membuat status history
- `[finished]` Sudah masuk strict regression test V5

## BE-53 [MVP] Technician reject request

Status: `[finished]`

Endpoint:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/reject
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "rejectReason": "Jadwal penuh"
}
```

Checklist:

- `[finished]` Hanya technician login
- `[finished]` Request harus milik technician login
- `[finished]` Hanya boleh dari status `WAITING`
- `[finished]` Simpan `rejectReason` jika dikirim
- `[finished]` Update status menjadi `REJECTED`
- `[finished]` DB trigger isi `rejectedAt`
- `[finished]` DB trigger membuat status history
- `[finished]` Sudah masuk strict regression test V5

## BE-54 [MVP] Technician mulai pengerjaan

Status: `[finished]`

Endpoint:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/start
Authorization: Bearer {technicianToken}
```

Checklist:

- `[finished]` Hanya technician login
- `[finished]` Request harus milik technician login
- `[finished]` Hanya boleh dari status `ACCEPTED`
- `[finished]` Update status menjadi `ON_PROGRESS`
- `[finished]` DB trigger isi `startedAt`
- `[finished]` DB trigger membuat status history
- `[finished]` Sudah masuk strict regression test V5

## BE-55 [MVP] Technician selesaikan pengerjaan

Status: `[finished]`

Endpoint:

```http
PATCH /api/technicians/service-requests/{serviceRequestId}/complete
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "finalCost": 150000,
  "technicianNote": "Freon ditambah dan filter dibersihkan"
}
```

Checklist:

- `[finished]` Hanya technician login
- `[finished]` Request harus milik technician login
- `[finished]` Hanya boleh dari status `ON_PROGRESS`
- `[finished]` Validasi `finalCost` jika dikirim
- `[finished]` Validasi `technicianNote` maksimal 1000 karakter
- `[finished]` Simpan `finalCost`
- `[finished]` Simpan `technicianNote`
- `[finished]` Update status menjadi `COMPLETED`
- `[finished]` DB trigger isi `completedAt`
- `[finished]` DB trigger membuat status history
- `[finished]` Sudah masuk strict regression test V5

## BE-56 [MVP] Technician lihat status history service request

Status: `[implemented]`

Endpoint:

```http
GET /api/technicians/service-requests/{serviceRequestId}/status-history
Authorization: Bearer {technicianToken}
```

Checklist:

- `[implemented]` Endpoint sudah ada
- `[implemented]` Hanya technician login
- `[implemented]` Technician hanya bisa melihat history request yang ditujukan kepadanya
- `[implemented]` Validasi UUID
- `[implemented]` Request milik technician lain return `404`
- `[implemented]` Sort by `createdAt ASC`
- `[implemented]` Response field English
- `[ongoing]` Tambahkan ke strict regression test

---

# 6. Status dan Riwayat Status

Target: semua perubahan status service request tercatat dan bisa dilihat sebagai timeline.

## BE-60 [MVP] Trigger status flow service request

Status: `[finished]`

Checklist:

- `[finished]` Initial status harus `WAITING`
- `[finished]` `WAITING` boleh ke `ACCEPTED`
- `[finished]` `WAITING` boleh ke `REJECTED`
- `[finished]` `WAITING` boleh ke `CANCELLED`
- `[finished]` `ACCEPTED` boleh ke `ON_PROGRESS`
- `[finished]` `ACCEPTED` boleh ke `CANCELLED`
- `[finished]` `ON_PROGRESS` boleh ke `COMPLETED`
- `[finished]` `ON_PROGRESS` boleh ke `CANCELLED`
- `[finished]` Status final tidak bisa diubah lagi

Final statuses:

```text
COMPLETED
CANCELLED
REJECTED
```

## BE-61 [MVP] Trigger status history otomatis

Status: `[finished]`

Checklist:

- `[finished]` Saat insert service request, otomatis insert row ke `riwayat_status`
- `[finished]` Saat status berubah, otomatis insert row ke `riwayat_status`
- `[finished]` `status_sebelum` tersimpan
- `[finished]` `status_sesudah` tersimpan
- `[finished]` `diubah_oleh` tersimpan dari `diubah_oleh_terakhir`
- `[finished]` Sudah dites create request menghasilkan `WAITING` history

Catatan penting:

- Jangan insert `RiwayatStatus` manual dari Java saat create/update status.
- Database trigger sudah menangani status history.

## BE-62 [MVP] API lihat timeline status request

Status: `[implemented]`

Endpoint:

```http
GET /api/customers/service-requests/{serviceRequestId}/status-history
GET /api/technicians/service-requests/{serviceRequestId}/status-history
```

Checklist:

- `[implemented]` Endpoint customer tersedia
- `[implemented]` Endpoint technician tersedia
- `[implemented]` Customer hanya bisa melihat history request miliknya
- `[implemented]` Technician hanya bisa melihat history request yang ditujukan kepadanya
- `[implemented]` Sort by `createdAt ASC`
- `[implemented]` Response field English
- `[ongoing]` Masukkan ke strict smoke test sebagai BE-63

## BE-63 [NEXT] Strict smoke test status history

Status: `[ongoing]`

Target:

- `[ongoing]` Test customer get status history setelah create request
- `[ongoing]` Pastikan history punya `WAITING`
- `[ongoing]` Test setelah accept, ada `ACCEPTED`
- `[ongoing]` Test setelah start, ada `ON_PROGRESS`
- `[ongoing]` Test setelah complete, ada `COMPLETED`
- `[ongoing]` Test technician get status history
- `[ongoing]` Test wrong role `403`
- `[ongoing]` Test invalid UUID `400`
- `[ongoing]` Test not owner / not assigned `404`

Setelah BE-63 lolos, BE-62 boleh naik dari `[implemented]` menjadi `[finished]`.

---

# 7. Profil User dan Technician

Target: customer dan technician bisa melihat serta mengubah profil dasar. Technician juga bisa mengatur availability status dan description.

## BE-70 [MVP] Update profil sendiri

Status: `[finished]`

Endpoint:

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

Checklist:

- `[finished]` Bisa update `name`
- `[finished]` Bisa update `phoneNumber`
- `[finished]` Bisa update `profilePhoto`
- `[finished]` Bisa update `address`
- `[finished]` Validasi phone number jika berubah
- `[finished]` Cegah duplicate phone number
- `[finished]` Response menggunakan field English
- `[todo]` Refactor request dari `Map<String,String>` ke DTO khusus agar validation lebih rapi

## BE-71 [NEXT] Technician update availability status

Status: `[ongoing]`

Endpoint planned:

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

Checklist:

- `[todo]` Buat DTO `UpdateTechnicianAvailabilityRequest`
- `[todo]` Endpoint hanya technician
- `[todo]` Customer token return `403`
- `[todo]` Tanpa token return `401`
- `[todo]` Validasi invalid status
- `[todo]` Update `status_ketersediaan` di `teknisi_profile`
- `[todo]` Response field English
- `[todo]` Masukkan ke strict smoke test

Allowed values:

```text
ONLINE
OFFLINE
BUSY
ON_LEAVE
```

Success `200`:

```json
{
  "success": true,
  "message": "Technician availability updated successfully",
  "data": {
    "technicianProfileId": "uuid",
    "availabilityStatus": "ONLINE"
  },
  "errors": null
}
```

## BE-72 [NEXT] Technician update deskripsi profil

Status: `[todo]`

Endpoint planned:

```http
PUT /api/technicians/profile
Authorization: Bearer {technicianToken}
Content-Type: application/json
```

Request:

```json
{
  "description": "Spesialis AC, kulkas, dan mesin cuci",
  "profilePhoto": "https://example.com/photo.jpg"
}
```

Checklist:

- `[todo]` Bisa update `description`
- `[todo]` Bisa update `profilePhoto` lewat data user
- `[todo]` Validasi description maksimal 1000 karakter
- `[todo]` Response menggunakan field English
- `[todo]` Masukkan ke strict smoke test

---

# 8. Review

Target: customer bisa memberi rating setelah service request selesai, dan customer lain bisa melihat review technician sebelum memilih technician.

## BE-80 [MVP] Review schema

Status: `[finished]`

Checklist:

- `[finished]` Migration `V6__create_reviews.sql`
- `[finished]` Tabel `review`
- `[finished]` Entity `Review`
- `[finished]` `ReviewRepository`
- `[finished]` Relasi ke `permintaan_layanan`
- `[finished]` Relasi ke customer
- `[finished]` Relasi ke technician profile
- `[finished]` Rating 1 sampai 5
- `[finished]` Satu request hanya boleh satu review
- `[finished]` Index review by technician profile

Columns:

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

## BE-81 [MVP] Customer membuat review

Status: `[implemented]`

Endpoint:

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

Checklist:

- `[implemented]` Endpoint tersedia
- `[implemented]` Request harus milik customer login
- `[implemented]` Request harus `COMPLETED`
- `[implemented]` Rating wajib 1 sampai 5
- `[implemented]` Comment opsional
- `[implemented]` Satu request hanya boleh satu review
- `[implemented]` Update `ratingAvg` technician
- `[implemented]` Update `ratingCount` technician
- `[ongoing]` Tambahkan strict smoke test create review success
- `[ongoing]` Tambahkan strict smoke test review sebelum completed harus `409`
- `[ongoing]` Tambahkan strict smoke test duplicate review harus `409`
- `[ongoing]` Tambahkan strict smoke test wrong owner harus `404`

## BE-82 [NEXT] Lihat review technician

Status: `[ongoing]`

Endpoint planned:

```http
GET /api/customers/technicians/{technicianProfileId}/reviews
Authorization: Bearer {customerToken}
```

Checklist:

- `[todo]` Return daftar review technician
- `[todo]` Validasi technician profile UUID
- `[todo]` Technician not found return `404`
- `[todo]` Return customer reviewer summary secukupnya
- `[todo]` Return `rating`, `comment`, `createdAt`
- `[todo]` Bisa pagination sederhana: `page`, `size`
- `[todo]` Default sort newest first
- `[todo]` Response field English
- `[todo]` Masukkan ke strict smoke test

Response planned:

```json
{
  "success": true,
  "message": "Technician reviews retrieved successfully",
  "data": [
    {
      "reviewId": "uuid",
      "serviceRequestId": "uuid",
      "customerId": "uuid",
      "customerName": "Customer Demo",
      "technicianProfileId": "uuid",
      "rating": 5,
      "comment": "Teknisi ramah",
      "createdAt": "2026-06-02T10:00:00+07:00"
    }
  ],
  "errors": null
}
```

## BE-83 [MVP] Rating summary technician

Status: `[implemented]`

Checklist:

- `[implemented]` `ratingAvg` technician diupdate saat create review
- `[implemented]` `ratingCount` technician diupdate saat create review
- `[todo]` Pastikan rating summary muncul di technician discovery setelah review dibuat
- `[todo]` Tambahkan strict smoke test perubahan `averageRating` dan `ratingCount`

## BE-84 [NEXT] Strict smoke test review

Status: `[ongoing]`

Target test:

- `[todo]` Create service request sampai completed
- `[todo]` Customer create review success `201`
- `[todo]` Response punya `reviewId`, `rating`, `comment`
- `[todo]` Duplicate review return `409`
- `[todo]` Review request yang belum completed return `409`
- `[todo]` Review request milik customer lain return `404`
- `[todo]` Invalid rating return `400`
- `[todo]` Rating technician berubah setelah review

Setelah BE-84 lolos, BE-81 dan BE-83 boleh naik menjadi `[finished]`.

---

# 9. Chat REST dan WebSocket

Target: customer dan technician terkait service request bisa berkomunikasi. Untuk MVP awal desktop, chat bisa dibuat REST dulu, WebSocket belakangan.

## BE-90 [LATER] Chat schema

Status: `[deferred]`

Checklist planned:

- `[deferred]` Tabel `pesan` atau `message`
- `[deferred]` Entity message
- `[deferred]` Repository message
- `[deferred]` Index by service request and created time
- `[deferred]` Message type minimal `TEXT`

## BE-91 [LATER] Kirim pesan text via REST

Status: `[deferred]`

Endpoint planned:

```http
POST /api/service-requests/{serviceRequestId}/messages
Authorization: Bearer {token}
Content-Type: application/json
```

Request planned:

```json
{
  "message": "Saya sudah di depan rumah"
}
```

Checklist planned:

- `[deferred]` Validasi user bagian dari request
- `[deferred]` Pesan text wajib punya `message`
- `[deferred]` Message type `TEXT`
- `[deferred]` Simpan sender
- `[deferred]` Simpan `sentAt`

## BE-92 [LATER] Ambil riwayat chat

Status: `[deferred]`

Endpoint planned:

```http
GET /api/service-requests/{serviceRequestId}/messages
Authorization: Bearer {token}
```

Checklist planned:

- `[deferred]` Validasi user bagian dari request
- `[deferred]` Sort by `createdAt ASC`
- `[deferred]` Pagination optional
- `[deferred]` Response field English

## BE-93 [LATER] WebSocket chat real-time

Status: `[deferred]`

Checklist planned:

- `[deferred]` WebSocket config
- `[deferred]` STOMP endpoint atau WebSocket endpoint sesuai kebutuhan
- `[deferred]` Subscribe room per service request
- `[deferred]` Auth token pada WebSocket connection
- `[deferred]` Broadcast pesan ke customer dan technician terkait

Catatan:

- Jangan mulai WebSocket sebelum REST chat stabil.
- Untuk demo desktop, REST chat sudah cukup jika waktu terbatas.

---

# 10. Notifikasi

Target: user mendapat informasi ketika ada request baru, status berubah, atau pesan baru.

## BE-100 [LATER] List notifikasi user

Status: `[deferred]`

Endpoint planned:

```http
GET /api/notifications
Authorization: Bearer {token}
```

Checklist planned:

- `[deferred]` Return notification user login
- `[deferred]` Sort newest first
- `[deferred]` Filter unread optional
- `[deferred]` Response field English

## BE-101 [LATER] Tandai notifikasi dibaca

Status: `[deferred]`

Endpoint planned:

```http
PATCH /api/notifications/{notificationId}/read
Authorization: Bearer {token}
```

Checklist planned:

- `[deferred]` Validasi notification milik user login
- `[deferred]` Isi `readAt`
- `[deferred]` Idempotent jika sudah read

## BE-102 [LATER] Buat notifikasi saat status berubah

Status: `[deferred]`

Checklist planned:

- `[deferred]` Saat customer membuat request, technician dapat notifikasi
- `[deferred]` Saat technician menerima request, customer dapat notifikasi
- `[deferred]` Saat technician menolak request, customer dapat notifikasi
- `[deferred]` Saat technician mulai kerja, customer dapat notifikasi
- `[deferred]` Saat technician menyelesaikan request, customer dapat notifikasi
- `[deferred]` Saat customer membatalkan request, technician dapat notifikasi

Catatan:

- Untuk MVP awal, status history sudah cukup sebagai pengganti notifikasi.
- Notifikasi dikerjakan setelah review + profile technician stabil.

---

# 11. Admin Opsional

Catatan: role `ADMIN` ada di backend, tetapi fokus MVP stable adalah customer dan technician. Admin dikerjakan setelah flow utama stabil.

## BE-110 [LATER] Admin CRUD device category

Status: `[deferred]`

Endpoint planned:

```http
GET    /api/admin/device-categories
POST   /api/admin/device-categories
PATCH  /api/admin/device-categories/{deviceCategoryId}
DELETE /api/admin/device-categories/{deviceCategoryId}
```

## BE-111 [LATER] Admin lihat semua user

Status: `[deferred]`

Endpoint planned:

```http
GET /api/admin/users
```

Checklist planned:

- `[deferred]` Filter role
- `[deferred]` Filter accountStatus
- `[deferred]` Search name/email

## BE-112 [LATER] Admin lihat semua service request

Status: `[deferred]`

Endpoint planned:

```http
GET /api/admin/service-requests
```

Checklist planned:

- `[deferred]` Filter status
- `[deferred]` Filter tanggal
- `[deferred]` Filter technician
- `[deferred]` Filter customer
- `[deferred]` Filter device category

---

# 12. Ringkasan Contract API

## 12.1 Endpoint yang sudah dibuat / tersedia

### Auth

```text
POST /api/auth/register/customer
POST /api/auth/register/technician
POST /api/auth/login
GET  /api/auth/profile
```

### User Profile

```text
PUT /api/users/me
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
GET   /api/customers/service-requests/{serviceRequestId}/status-history
PATCH /api/customers/service-requests/{serviceRequestId}/cancel
POST  /api/customers/service-requests/{serviceRequestId}/review
```

### Technician Service Request

```text
GET   /api/technicians/service-requests
GET   /api/technicians/service-requests/{serviceRequestId}
GET   /api/technicians/service-requests/{serviceRequestId}/status-history
PATCH /api/technicians/service-requests/{serviceRequestId}/accept
PATCH /api/technicians/service-requests/{serviceRequestId}/reject
PATCH /api/technicians/service-requests/{serviceRequestId}/start
PATCH /api/technicians/service-requests/{serviceRequestId}/complete
```

## 12.2 Endpoint next immediate

### Status History Testing

```text
GET /api/customers/service-requests/{serviceRequestId}/status-history
GET /api/technicians/service-requests/{serviceRequestId}/status-history
```

Keterangan: endpoint sudah ada, next-nya adalah strict smoke test.

### Review Testing dan Read Review

```text
POST /api/customers/service-requests/{serviceRequestId}/review
GET  /api/customers/technicians/{technicianProfileId}/reviews
```

Keterangan: create review sudah ada, next-nya strict smoke test + list review.

### Technician Profile

```text
PATCH /api/technicians/availability
PUT   /api/technicians/profile
```

## 12.3 Endpoint deferred

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
PUT /api/users/me
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

- `[ ]` Success case
- `[ ]` Tanpa token jika endpoint protected
- `[ ]` Wrong role jika endpoint role-based
- `[ ]` Invalid UUID jika endpoint memakai path/query UUID
- `[ ]` Not found jika data tidak ada
- `[ ]` Forbidden ownership jika data bukan milik user login
- `[ ]` Validation error jika request body tidak valid
- `[ ]` Invalid enum jika memakai enum
- `[ ]` Invalid status transition jika endpoint update status
- `[ ]` Response sukses memakai `ApiResponse`
- `[ ]` Response error memakai `ApiResponse`
- `[ ]` Tambahkan ke strict regression test `develop/api-smoke-test.sh`

Strict regression test saat ini:

```bash
bash develop/api-smoke-test.sh
```

Target terakhir yang sudah tercapai pada roadmap lama:

```text
ALL STRICT API SMOKE TESTS V5 PASSED
Passed: 736
Failed: 0
```

Catatan:

- Karena status history dan create review sudah muncul di kode, strict smoke test berikutnya harus naik dari V5 ke V6 atau update `api-smoke-test.sh` aktif.
- Setelah update test, ubah status BE-44, BE-45, BE-56, BE-62, BE-81, dan BE-83 sesuai hasil test.

---

# 15. Urutan Pengerjaan Terdekat

Status terakhir yang sudah solid:

```text
[finished] BE-41 Customer List Service Requests
[finished] BE-42 Customer Detail Service Request
[finished] BE-43 Customer Cancel Service Request
[finished] BE-50 Technician List Service Requests
[finished] BE-51 Technician Detail Service Request
[finished] BE-52 Technician Accept Request
[finished] BE-53 Technician Reject Request
[finished] BE-54 Technician Start Work
[finished] BE-55 Technician Complete Work
[implemented] BE-62 Status History Read API
[implemented] BE-81 Create Review
```

Urutan paling aman dari posisi sekarang:

```text
1. BE-63 Strict smoke test status history
2. BE-84 Strict smoke test create review
3. BE-82 List review technician
4. BE-71 Technician update availability status
5. BE-72 Technician update profile/description
6. Refactor BE-70 update profile request Map -> DTO
7. Putuskan refresh token + logout server-side tetap deferred atau naik ke MVP+
8. Chat REST send message
9. Chat REST history
10. WebSocket chat
11. Notification
12. Admin opsional
```

Alasan prioritas:

- Status history dan review sudah ada di code, jadi yang paling berbahaya adalah membiarkannya tanpa regression test.
- List review diperlukan oleh frontend sebelum customer memilih technician.
- Availability technician diperlukan karena customer discovery sudah punya filter availability.
- Chat dan notification jangan dikerjakan sebelum service request + review stabil.

---

# 16. Catatan Penting untuk Tim

- Jangan pakai `JenisLayanan` untuk flow MVP stable.
- Jangan buat endpoint khusus desktop atau khusus Android.
- Jangan expose entity JPA langsung ke response.
- Jangan insert status history manual saat create/update status request; database trigger sudah menangani.
- Semua API field harus English.
- Semua response harus memakai `ApiResponse<T>`.
- Semua protected endpoint harus dites `401` dan `403`.
- Commit per phase supaya mudah rollback.
- Jangan commit file lokal seperti `.env`, `.idea`, `.vscode`, `bin`, `build`, atau SQL session lokal.
- Jangan ignore `develop/api-smoke-test.sh` jika script test perlu masuk repository.
- `*.txt` boleh di-ignore untuk dump lokal, tetapi jangan menyimpan dokumen penting proyek sebagai `.txt` jika rule ini tetap ada.
- Simpan roadmap sebagai `.md`, misalnya `src/main/Roadmap.md` atau `docs/backend-roadmap.md`.

---

# 17. Catatan `.gitignore`

Perlu dicek ulang bagian ini:

```gitignore
# Dev
*.txt
*.sh
```

Masalah:

- `*.sh` akan membuat script seperti `develop/api-smoke-test.sh` tidak ikut commit.
- Padahal script smoke test adalah bagian penting regression testing.

Saran update:

```gitignore
# Local temporary dumps
semua_isi_file_backend*.txt
run-ini.txt

# Jangan ignore semua script shell, karena smoke test perlu di-commit
# *.sh
```

Atau jika tetap ingin ignore script lokal:

```gitignore
*.local.sh
!develop/*.sh
```

---

# 18. Commit Convention

Contoh commit per phase:

```bash
git add .
git commit -m "phase 6a add status history smoke tests"
```

```bash
git add .
git commit -m "phase 8a add create review smoke tests"
```

```bash
git add .
git commit -m "phase 8b add technician review list api"
```

Contoh commit dokumentasi:

```bash
git add src/main/Roadmap.md
git commit -m "docs update backend roadmap and api contract"
```

---

# 19. Definition of Done MVP Backend

Backend MVP dianggap stabil jika semua poin ini selesai:

```text
[finished] Auth register/login/profile
[finished] Device categories
[finished] Technician skill/device categories
[finished] Customer technician discovery
[finished] Customer create/list/detail/cancel service request
[finished] Technician list/detail/accept/reject/start/complete service request
[finished] Status history read API + strict smoke test
[finished] Customer create review + strict smoke test
[finished] Technician review list API
[finished] Technician availability update
[finished] Strict regression test final pass
```

Fitur yang boleh berada di luar MVP stable:

```text
[deferred] Refresh token/logout server-side
[deferred] Chat REST/WebSocket
[deferred] Notification
[deferred] Admin dashboard API
[deferred] GPS/latitude/longitude flow
[deferred] Jenis layanan detail/harga paket
```

---

# 20. Next Action Checklist

Checklist kerja langsung setelah roadmap ini di-commit:

```text
[ ] Update develop/api-smoke-test.sh untuk status history
[ ] Jalankan bash develop/api-smoke-test.sh
[ ] Jika pass, ubah BE-62 menjadi [finished]
[ ] Update develop/api-smoke-test.sh untuk create review
[ ] Test create review success setelah completed
[ ] Test review sebelum completed -> 409
[ ] Test duplicate review -> 409
[ ] Test invalid rating -> 400
[ ] Jika pass, ubah BE-81 dan BE-83 menjadi [finished]
[ ] Buat endpoint GET /api/customers/technicians/{technicianProfileId}/reviews
[ ] Buat endpoint PATCH /api/technicians/availability
[ ] Hapus ignore *.sh dari .gitignore atau whitelist develop/*.sh
```
