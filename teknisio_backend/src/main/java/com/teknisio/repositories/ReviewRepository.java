package com.teknisio.repositories;

import com.teknisio.model.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

  boolean existsByPermintaan_IdPermintaan(UUID idPermintaan);
}