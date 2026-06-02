-- ============================================================
-- Teknisio Migration V4: Functions and triggers
-- Clean MVP schema
-- ============================================================

-- ============================================================
-- FUNCTION: update updated_at automatically
-- ============================================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- FUNCTION: validate service request status flow
--
-- Valid flow:
-- WAITING     -> ACCEPTED / REJECTED / CANCELLED
-- ACCEPTED   -> ON_PROGRESS / CANCELLED
-- ON_PROGRESS -> COMPLETED / CANCELLED
--
-- Final statuses:
-- COMPLETED, CANCELLED, REJECTED
-- ============================================================
CREATE OR REPLACE FUNCTION validate_request_status_flow()
RETURNS TRIGGER AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    NEW.status := COALESCE(NEW.status, 'WAITING');
    NEW.waktu_permintaan := COALESCE(NEW.waktu_permintaan, CURRENT_TIMESTAMP);

    IF NEW.status <> 'WAITING' THEN
      RAISE EXCEPTION 'Initial service request status must be WAITING';
    END IF;

    RETURN NEW;
  END IF;

  IF NEW.status IS DISTINCT FROM OLD.status THEN
    IF OLD.status = 'WAITING'
      AND NEW.status NOT IN ('ACCEPTED', 'REJECTED', 'CANCELLED') THEN
      RAISE EXCEPTION 'Invalid status transition from WAITING to %', NEW.status;
    END IF;

    IF OLD.status = 'ACCEPTED'
      AND NEW.status NOT IN ('ON_PROGRESS', 'CANCELLED') THEN
      RAISE EXCEPTION 'Invalid status transition from ACCEPTED to %', NEW.status;
    END IF;

    IF OLD.status = 'ON_PROGRESS'
      AND NEW.status NOT IN ('COMPLETED', 'CANCELLED') THEN
      RAISE EXCEPTION 'Invalid status transition from ON_PROGRESS to %', NEW.status;
    END IF;

    IF OLD.status IN ('COMPLETED', 'CANCELLED', 'REJECTED') THEN
      RAISE EXCEPTION 'Status % is final and cannot be changed', OLD.status;
    END IF;

    IF NEW.status = 'ACCEPTED' THEN
      NEW.waktu_diterima := COALESCE(NEW.waktu_diterima, CURRENT_TIMESTAMP);
    ELSIF NEW.status = 'ON_PROGRESS' THEN
      NEW.waktu_diproses := COALESCE(NEW.waktu_diproses, CURRENT_TIMESTAMP);
    ELSIF NEW.status = 'COMPLETED' THEN
      NEW.waktu_selesai := COALESCE(NEW.waktu_selesai, CURRENT_TIMESTAMP);
    ELSIF NEW.status = 'CANCELLED' THEN
      NEW.waktu_dibatalkan := COALESCE(NEW.waktu_dibatalkan, CURRENT_TIMESTAMP);
    ELSIF NEW.status = 'REJECTED' THEN
      NEW.waktu_ditolak := COALESCE(NEW.waktu_ditolak, CURRENT_TIMESTAMP);
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- FUNCTION: create status history automatically
-- ============================================================
CREATE OR REPLACE FUNCTION create_status_history()
RETURNS TRIGGER AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    INSERT INTO riwayat_status (
      id_permintaan,
      diubah_oleh,
      status_sebelum,
      status_sesudah,
      catatan
    ) VALUES (
      NEW.id_permintaan,
      NEW.diubah_oleh_terakhir,
      NULL,
      NEW.status,
      'Service request created'
    );

    RETURN NEW;
  END IF;

  IF NEW.status IS DISTINCT FROM OLD.status THEN
    INSERT INTO riwayat_status (
      id_permintaan,
      diubah_oleh,
      status_sebelum,
      status_sesudah,
      catatan
    ) VALUES (
      NEW.id_permintaan,
      NEW.diubah_oleh_terakhir,
      OLD.status,
      NEW.status,
      'Service request status updated'
    );
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- TRIGGERS: updated_at
-- ============================================================

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_teknisi_profile_updated_at
BEFORE UPDATE ON teknisi_profile
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_kategori_layanan_updated_at
BEFORE UPDATE ON kategori_layanan
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_teknisi_kategori_layanan_updated_at
BEFORE UPDATE ON teknisi_kategori_layanan
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_permintaan_layanan_updated_at
BEFORE UPDATE ON permintaan_layanan
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_user_session_updated_at
BEFORE UPDATE ON user_session
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- ============================================================
-- TRIGGERS: service request status rules
-- ============================================================

CREATE TRIGGER trg_permintaan_status_flow
BEFORE INSERT OR UPDATE OF status ON permintaan_layanan
FOR EACH ROW
EXECUTE FUNCTION validate_request_status_flow();

CREATE TRIGGER trg_permintaan_status_history
AFTER INSERT OR UPDATE OF status ON permintaan_layanan
FOR EACH ROW
EXECUTE FUNCTION create_status_history();
