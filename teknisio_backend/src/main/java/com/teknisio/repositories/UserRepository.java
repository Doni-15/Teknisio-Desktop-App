package com.teknisio.repositories;

import com.teknisio.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByIdUserAndDeletedAtIsNull(UUID idUser);

  Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);

  Optional<User> findByNoTeleponAndDeletedAtIsNull(String noTelepon);

  boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);

  boolean existsByNoTeleponAndDeletedAtIsNull(String noTelepon);

  boolean existsByEmailIgnoreCaseAndIdUserNotAndDeletedAtIsNull(String email, UUID idUser);

  boolean existsByNoTeleponAndIdUserNotAndDeletedAtIsNull(String noTelepon, UUID idUser);
}
