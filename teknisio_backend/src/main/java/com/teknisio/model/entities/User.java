package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseSoftDeletableAuditableEntity;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseSoftDeletableAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_user", nullable = false, updatable = false)
  private UUID idUser;

  @Column(name = "nama", nullable = false, length = 100)
  private String nama;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Column(name = "no_telepon", nullable = false, length = 20)
  private String noTelepon;

  @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
  private String passwordHash;

  @Column(name = "foto_profil", columnDefinition = "TEXT")
  private String fotoProfil;

  @Column(name = "alamat", columnDefinition = "TEXT")
  private String alamat;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "role", nullable = false, columnDefinition = "user_role")
  private UserRole role;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status_akun", nullable = false, columnDefinition = "user_status")
  @Builder.Default
  private UserStatus statusAkun = UserStatus.ACTIVE;

  @Column(name = "last_login")
  private OffsetDateTime lastLogin;
}
