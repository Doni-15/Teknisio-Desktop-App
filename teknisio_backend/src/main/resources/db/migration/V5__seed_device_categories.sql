-- ============================================================
-- Teknisio Migration V5: Seed default device categories
-- Clean MVP schema
-- ============================================================

INSERT INTO kategori_layanan (nama_kategori, icon, aktif)
VALUES
  ('Air Conditioner', 'air-conditioner', TRUE),
  ('Refrigerator', 'refrigerator', TRUE),
  ('Washing Machine', 'washing-machine', TRUE),
  ('Television', 'television', TRUE),
  ('Fan', 'fan', TRUE),
  ('Rice Cooker', 'rice-cooker', TRUE),
  ('Oven', 'oven', TRUE),
  ('Mixer', 'mixer', TRUE)
ON CONFLICT (LOWER(nama_kategori))
WHERE deleted_at IS NULL
DO UPDATE SET
  icon = EXCLUDED.icon,
  aktif = TRUE,
  updated_at = CURRENT_TIMESTAMP;
