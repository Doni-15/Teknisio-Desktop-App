-- ============================================================
-- Teknisio Migration V2: Core tables
-- ============================================================

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
  id_user        UUID PRIMARY KEY DEFAULT gen_random_uuid(),

  nama           VARCHAR(100) NOT NULL,
  email          VARCHAR(100) NOT NULL,
  no_telepon     VARCHAR(20) NOT NULL,
  password_hash  TEXT NOT NULL,
  foto_profil    TEXT,
  alamat         TEXT,

  role           user_role NOT NULL,
  status_akun    user_status NOT NULL DEFAULT 'ACTIVE',

  created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login     TIMESTAMPTZ,
  deleted_at     TIMESTAMPTZ,

  CONSTRAINT chk_users_email_format
    CHECK (email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$'),

  CONSTRAINT chk_users_no_telepon_format
    CHECK (no_telepon ~ '^\+?[0-9]{10,15}$')
);

-- ============================================================
-- TEKNISI PROFILE
-- One TECHNICIAN user has one technician profile.
-- ============================================================
CREATE TABLE teknisi_profile (
  id_teknisi_profile   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  id_user              UUID NOT NULL UNIQUE,

  status_ketersediaan  teknisi_status NOT NULL DEFAULT 'OFFLINE',
  rating_avg           DECIMAL(3, 2) NOT NULL DEFAULT 0 CHECK (rating_avg BETWEEN 0 AND 5),
  rating_count         INTEGER NOT NULL DEFAULT 0 CHECK (rating_count >= 0),
  total_pekerjaan      INTEGER NOT NULL DEFAULT 0 CHECK (total_pekerjaan >= 0),
  deskripsi            TEXT,

  created_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_teknisi_profile_user
    FOREIGN KEY (id_user)
    REFERENCES users (id_user)
    ON DELETE CASCADE
);

-- ============================================================
-- KATEGORI LAYANAN / DEVICE CATEGORY
-- Example: Air Conditioner, Refrigerator, Washing Machine.
-- ============================================================
CREATE TABLE kategori_layanan (
  id_kategori    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nama_kategori  VARCHAR(100) NOT NULL,
  icon           TEXT,
  aktif          BOOLEAN NOT NULL DEFAULT TRUE,

  created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at     TIMESTAMPTZ
);

-- ============================================================
-- TEKNISI KATEGORI LAYANAN / TECHNICIAN DEVICE CATEGORY
-- Technician skills by device category.
-- ============================================================
CREATE TABLE teknisi_kategori_layanan (
  id_teknisi_profile  UUID NOT NULL,
  id_kategori         UUID NOT NULL,
  aktif               BOOLEAN NOT NULL DEFAULT TRUE,

  created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id_teknisi_profile, id_kategori),

  CONSTRAINT fk_teknisi_kategori_teknisi
    FOREIGN KEY (id_teknisi_profile)
    REFERENCES teknisi_profile (id_teknisi_profile)
    ON DELETE CASCADE,

  CONSTRAINT fk_teknisi_kategori_kategori
    FOREIGN KEY (id_kategori)
    REFERENCES kategori_layanan (id_kategori)
    ON DELETE CASCADE
);

-- ============================================================
-- PERMINTAAN LAYANAN / SERVICE REQUEST
-- Customer chooses one technician and one or more device categories.
-- Selected categories are stored in permintaan_layanan_kategori.
-- ============================================================
CREATE TABLE permintaan_layanan (
  id_permintaan          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

  kode_permintaan        VARCHAR(50) NOT NULL UNIQUE DEFAULT (
    'REQ-' ||
    TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD') ||
    '-' ||
    UPPER(SUBSTRING(REPLACE(gen_random_uuid()::TEXT, '-', '') FROM 1 FOR 8))
  ),

  id_pengguna            UUID NOT NULL,
  id_teknisi_profile     UUID NOT NULL,

  latitude               DECIMAL(10, 7),
  longitude              DECIMAL(10, 7),
  alamat                 TEXT NOT NULL,
  detail_alamat          TEXT,
  deskripsi_masalah      TEXT NOT NULL,

  status                 request_status NOT NULL DEFAULT 'WAITING',

  estimasi_biaya         NUMERIC(12, 2) CHECK (estimasi_biaya IS NULL OR estimasi_biaya >= 0),
  biaya_akhir            NUMERIC(12, 2) CHECK (biaya_akhir IS NULL OR biaya_akhir >= 0),
  catatan_teknisi        TEXT,
  alasan_batal           TEXT,
  alasan_tolak           TEXT,

  waktu_permintaan       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  waktu_diterima         TIMESTAMPTZ,
  waktu_diproses         TIMESTAMPTZ,
  waktu_selesai          TIMESTAMPTZ,
  waktu_dibatalkan       TIMESTAMPTZ,
  waktu_ditolak          TIMESTAMPTZ,

  diubah_oleh_terakhir   UUID,

  created_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_permintaan_pengguna
    FOREIGN KEY (id_pengguna)
    REFERENCES users (id_user)
    ON DELETE RESTRICT,

  CONSTRAINT fk_permintaan_teknisi_profile
    FOREIGN KEY (id_teknisi_profile)
    REFERENCES teknisi_profile (id_teknisi_profile)
    ON DELETE RESTRICT,

  CONSTRAINT fk_permintaan_diubah_oleh
    FOREIGN KEY (diubah_oleh_terakhir)
    REFERENCES users (id_user)
    ON DELETE SET NULL,

  CONSTRAINT chk_permintaan_latitude
    CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),

  CONSTRAINT chk_permintaan_longitude
    CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),

  CONSTRAINT chk_permintaan_biaya_akhir
    CHECK (
      estimasi_biaya IS NULL
      OR biaya_akhir IS NULL
      OR biaya_akhir >= 0
    )
);

-- ============================================================
-- PERMINTAAN LAYANAN KATEGORI / SELECTED DEVICE CATEGORIES
-- One service request can contain multiple selected device categories.
-- ============================================================
CREATE TABLE permintaan_layanan_kategori (
  id_permintaan  UUID NOT NULL,
  id_kategori    UUID NOT NULL,

  created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id_permintaan, id_kategori),

  CONSTRAINT fk_permintaan_kategori_permintaan
    FOREIGN KEY (id_permintaan)
    REFERENCES permintaan_layanan (id_permintaan)
    ON DELETE CASCADE,

  CONSTRAINT fk_permintaan_kategori_kategori
    FOREIGN KEY (id_kategori)
    REFERENCES kategori_layanan (id_kategori)
    ON DELETE RESTRICT
);

-- ============================================================
-- RIWAYAT STATUS / STATUS HISTORY
-- Audit trail for service request status changes.
-- ============================================================
CREATE TABLE riwayat_status (
  id_riwayat      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  id_permintaan   UUID NOT NULL,
  diubah_oleh     UUID,

  status_sebelum  request_status,
  status_sesudah  request_status NOT NULL,
  catatan         TEXT,

  created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_riwayat_status_permintaan
    FOREIGN KEY (id_permintaan)
    REFERENCES permintaan_layanan (id_permintaan)
    ON DELETE CASCADE,

  CONSTRAINT fk_riwayat_status_user
    FOREIGN KEY (diubah_oleh)
    REFERENCES users (id_user)
    ON DELETE SET NULL
);

-- ============================================================
-- USER SESSION
-- Refresh token session storage.
-- ============================================================
CREATE TABLE user_session (
  id_session          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  id_user             UUID NOT NULL,
  refresh_token_hash  TEXT NOT NULL UNIQUE,
  expired_at          TIMESTAMPTZ NOT NULL,
  revoked_at          TIMESTAMPTZ,
  device_info         TEXT,
  ip_address          VARCHAR(100),

  created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_user_session_user
    FOREIGN KEY (id_user)
    REFERENCES users (id_user)
    ON DELETE CASCADE
);
