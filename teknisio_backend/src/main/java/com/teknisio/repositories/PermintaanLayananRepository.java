package com.teknisio.repositories;

import com.teknisio.model.entities.PermintaanLayanan;
import com.teknisio.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PermintaanLayananRepository extends JpaRepository<PermintaanLayanan, UUID> {

  List<PermintaanLayanan> findByPengguna_IdUserOrderByWaktuPermintaanDesc(UUID idUser);

  List<PermintaanLayanan> findByPengguna_IdUserAndStatusOrderByWaktuPermintaanDesc(
    UUID idUser,
    RequestStatus status
  );

  List<PermintaanLayanan> findByTeknisiProfile_IdTeknisiProfileOrderByWaktuPermintaanDesc(UUID idTeknisiProfile);

  List<PermintaanLayanan> findByTeknisiProfile_IdTeknisiProfileAndStatusOrderByWaktuPermintaanDesc(
    UUID idTeknisiProfile,
    RequestStatus status
  );
}
