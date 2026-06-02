-- ============================================================
-- Teknisio Migration V3: Indexes
-- Clean MVP schema
-- ============================================================

-- ============================================================
-- UNIQUE INDEXES
-- ============================================================

CREATE UNIQUE INDEX uq_users_email_active
  ON users (LOWER(email))
  WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uq_users_no_telepon_active
  ON users (no_telepon)
  WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uq_kategori_layanan_nama_active
  ON kategori_layanan (LOWER(nama_kategori))
  WHERE deleted_at IS NULL;

-- ============================================================
-- LOOKUP / FILTER INDEXES
-- ============================================================

CREATE INDEX idx_users_role_status
  ON users (role, status_akun)
  WHERE deleted_at IS NULL;

CREATE INDEX idx_teknisi_profile_user
  ON teknisi_profile (id_user);

CREATE INDEX idx_teknisi_profile_status
  ON teknisi_profile (status_ketersediaan);

CREATE INDEX idx_kategori_layanan_active
  ON kategori_layanan (aktif)
  WHERE deleted_at IS NULL;

CREATE INDEX idx_teknisi_kategori_kategori
  ON teknisi_kategori_layanan (id_kategori)
  WHERE aktif = TRUE;

CREATE INDEX idx_teknisi_kategori_teknisi
  ON teknisi_kategori_layanan (id_teknisi_profile)
  WHERE aktif = TRUE;

CREATE INDEX idx_permintaan_pengguna
  ON permintaan_layanan (id_pengguna);

CREATE INDEX idx_permintaan_teknisi_profile
  ON permintaan_layanan (id_teknisi_profile);

CREATE INDEX idx_permintaan_status
  ON permintaan_layanan (status);

CREATE INDEX idx_permintaan_waktu
  ON permintaan_layanan (waktu_permintaan DESC);

CREATE INDEX idx_permintaan_kategori_kategori
  ON permintaan_layanan_kategori (id_kategori);

CREATE INDEX idx_riwayat_status_permintaan
  ON riwayat_status (id_permintaan, created_at ASC);

CREATE INDEX idx_user_session_user
  ON user_session (id_user);

CREATE INDEX idx_user_session_expired_at
  ON user_session (expired_at);

CREATE INDEX idx_user_session_active
  ON user_session (id_user, expired_at)
  WHERE revoked_at IS NULL;
