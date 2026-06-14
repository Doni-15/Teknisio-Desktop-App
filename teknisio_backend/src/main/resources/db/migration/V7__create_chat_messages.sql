-- ============================================================
-- Teknisio Migration V7: Chat Messages
-- H2 & PostgreSQL compatible
-- ============================================================

CREATE TABLE pesan_chat (
  id_pesan             UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
  id_permintaan        UUID NOT NULL,
  id_pengirim          UUID NOT NULL,
  isi                  TEXT NOT NULL,
  created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_pesan_chat_permintaan
    FOREIGN KEY (id_permintaan)
    REFERENCES permintaan_layanan (id_permintaan)
    ON DELETE CASCADE,

  CONSTRAINT fk_pesan_chat_pengirim
    FOREIGN KEY (id_pengirim)
    REFERENCES users (id_user)
    ON DELETE CASCADE
);

CREATE INDEX idx_pesan_chat_permintaan
  ON pesan_chat (id_permintaan);

CREATE INDEX idx_pesan_chat_created_at
  ON pesan_chat (created_at ASC);
