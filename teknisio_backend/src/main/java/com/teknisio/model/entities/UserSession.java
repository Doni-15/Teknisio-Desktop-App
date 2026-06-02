package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_session")
public class UserSession extends BaseAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_session", nullable = false, updatable = false)
  private UUID idSession;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_user", nullable = false)
  private User user;

  @Column(name = "refresh_token_hash", nullable = false, unique = true, columnDefinition = "TEXT")
  private String refreshTokenHash;

  @Column(name = "expired_at", nullable = false)
  private OffsetDateTime expiredAt;

  @Column(name = "revoked_at")
  private OffsetDateTime revokedAt;

  @Column(name = "device_info", columnDefinition = "TEXT")
  private String deviceInfo;

  @Column(name = "ip_address", length = 100)
  private String ipAddress;
}
