package com.teknisio.repositories;

import com.teknisio.model.entities.KategoriLayanan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KategoriLayananRepository extends JpaRepository<KategoriLayanan, UUID> {

  List<KategoriLayanan> findByAktifTrueAndDeletedAtIsNullOrderByNamaKategoriAsc();

  Optional<KategoriLayanan> findByIdKategoriAndAktifTrueAndDeletedAtIsNull(UUID idKategori);

  boolean existsByIdKategoriAndAktifTrueAndDeletedAtIsNull(UUID idKategori);
}
