package com.teknisio.repositories;

import com.teknisio.model.entities.TeknisiKategoriLayanan;
import com.teknisio.model.entities.TeknisiKategoriLayananId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeknisiKategoriLayananRepository extends JpaRepository<TeknisiKategoriLayanan, TeknisiKategoriLayananId> {

  List<TeknisiKategoriLayanan> findByKategori_IdKategoriAndAktifTrue(UUID idKategori);

  List<TeknisiKategoriLayanan> findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(UUID idTeknisiProfile);

  boolean existsByTeknisiProfile_IdTeknisiProfileAndKategori_IdKategoriAndAktifTrue(
    UUID idTeknisiProfile,
    UUID idKategori
  );
}
