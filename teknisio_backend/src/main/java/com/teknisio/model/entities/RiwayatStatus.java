package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseCreatedAtEntity;
import com.teknisio.model.enums.RequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "riwayat_status")
public class RiwayatStatus extends BaseCreatedAtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_riwayat", nullable = false, updatable = false)
  private UUID idRiwayat;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_permintaan", nullable = false)
  private PermintaanLayanan permintaan;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diubah_oleh")
  private User diubahOleh;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status_sebelum", columnDefinition = "request_status")
  private RequestStatus statusSebelum;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status_sesudah", nullable = false, columnDefinition = "request_status")
  private RequestStatus statusSesudah;

  @Column(name = "catatan", columnDefinition = "TEXT")
  private String catatan;
}
