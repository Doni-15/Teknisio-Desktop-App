package com.teknisio.repositories;

import com.teknisio.model.entities.PesanChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PesanChatRepository extends JpaRepository<PesanChat, UUID> {

  List<PesanChat> findByPermintaanLayanan_IdPermintaanOrderByCreatedAtAsc(UUID idPermintaan);
}
