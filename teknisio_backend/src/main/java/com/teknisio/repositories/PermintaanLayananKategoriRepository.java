package com.teknisio.repositories;

import com.teknisio.model.entities.PermintaanLayananKategori;
import com.teknisio.model.entities.PermintaanLayananKategoriId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PermintaanLayananKategoriRepository extends JpaRepository<PermintaanLayananKategori, PermintaanLayananKategoriId> {

  List<PermintaanLayananKategori> findByPermintaan_IdPermintaan(UUID idPermintaan);

  boolean existsByPermintaan_IdPermintaanAndKategori_IdKategori(UUID idPermintaan, UUID idKategori);
}
