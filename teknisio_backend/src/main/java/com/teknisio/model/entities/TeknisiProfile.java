package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseAuditableEntity;
import com.teknisio.model.enums.TeknisiStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teknisi_profile")
public class TeknisiProfile extends BaseAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_teknisi_profile", nullable = false, updatable = false)
  private UUID idTeknisiProfile;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_user", nullable = false, unique = true)
  private User user;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status_ketersediaan", nullable = false, columnDefinition = "teknisi_status")
  @Builder.Default
  private TeknisiStatus statusKetersediaan = TeknisiStatus.OFFLINE;

  @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
  @Builder.Default
  private BigDecimal ratingAvg = BigDecimal.ZERO;

  @Column(name = "rating_count", nullable = false)
  @Builder.Default
  private Integer ratingCount = 0;

  @Column(name = "total_pekerjaan", nullable = false)
  @Builder.Default
  private Integer totalPekerjaan = 0;

  @Column(name = "deskripsi", columnDefinition = "TEXT")
  private String deskripsi;
}
