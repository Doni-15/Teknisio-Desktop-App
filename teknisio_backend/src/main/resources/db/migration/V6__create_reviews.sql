-- ============================================================
-- Teknisio Migration V6: Reviews
-- ============================================================

CREATE TABLE review (
  id_review            UUID PRIMARY KEY DEFAULT gen_random_uuid(),

  id_permintaan        UUID NOT NULL UNIQUE,
  id_customer          UUID NOT NULL,
  id_teknisi_profile   UUID NOT NULL,

  rating               INTEGER NOT NULL,
  comment              TEXT,

  created_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at           TIMESTAMPTZ,

  CONSTRAINT fk_review_permintaan
    FOREIGN KEY (id_permintaan)
    REFERENCES permintaan_layanan (id_permintaan)
    ON DELETE CASCADE,

  CONSTRAINT fk_review_customer
    FOREIGN KEY (id_customer)
    REFERENCES users (id_user)
    ON DELETE RESTRICT,

  CONSTRAINT fk_review_teknisi_profile
    FOREIGN KEY (id_teknisi_profile)
    REFERENCES teknisi_profile (id_teknisi_profile)
    ON DELETE RESTRICT,

  CONSTRAINT chk_review_rating
    CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_review_customer
  ON review (id_customer);

CREATE INDEX idx_review_teknisi_profile
  ON review (id_teknisi_profile);

CREATE INDEX idx_review_created_at
  ON review (created_at DESC);

CREATE TRIGGER trg_review_updated_at
BEFORE UPDATE ON review
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();