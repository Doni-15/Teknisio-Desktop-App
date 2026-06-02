package com.teknisio.repositories;

import com.teknisio.model.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

  Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

  Optional<UserSession> findByRefreshTokenHashAndRevokedAtIsNull(String refreshTokenHash);

  List<UserSession> findByUser_IdUserAndRevokedAtIsNull(UUID idUser);
}
