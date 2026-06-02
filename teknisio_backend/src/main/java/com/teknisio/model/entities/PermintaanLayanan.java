package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseAuditableEntity;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permintaan_layanan")
public class PermintaanLayanan extends BaseAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_permintaan", nullable = false, updatable = false)
  private UUID idPermintaan;

  @Generated(event = EventType.INSERT)
  @Column(
    name = "kode_permintaan",
    nullable = false,
    unique = true,
    length = 50,
    insertable = false,
    updatable = false
  )
  private String kodePermintaan;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_pengguna", nullable = false)
  private User pengguna;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_teknisi_profile", nullable = false)
  private TeknisiProfile teknisiProfile;

  @Column(name = "latitude", precision = 10, scale = 7)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 10, scale = 7)
  private BigDecimal longitude;

  @Column(name = "alamat", nullable = false, columnDefinition = "TEXT")
  private String alamat;

  @Column(name = "detail_alamat", columnDefinition = "TEXT")
  private String detailAlamat;

  @Column(name = "deskripsi_masalah", nullable = false, columnDefinition = "TEXT")
  private String deskripsiMasalah;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status", nullable = false, columnDefinition = "request_status")
  @Builder.Default
  private RequestStatus status = RequestStatus.WAITING;

  @Column(name = "estimasi_biaya", precision = 12, scale = 2)
  private BigDecimal estimasiBiaya;

  @Column(name = "biaya_akhir", precision = 12, scale = 2)
  private BigDecimal biayaAkhir;

  @Column(name = "catatan_teknisi", columnDefinition = "TEXT")
  private String catatanTeknisi;

  @Column(name = "alasan_batal", columnDefinition = "TEXT")
  private String alasanBatal;

  @Column(name = "alasan_tolak", columnDefinition = "TEXT")
  private String alasanTolak;

  @Column(name = "waktu_permintaan", nullable = false)
  private OffsetDateTime waktuPermintaan;

  @Column(name = "waktu_diterima")
  private OffsetDateTime waktuDiterima;

  @Column(name = "waktu_diproses")
  private OffsetDateTime waktuDiproses;

  @Column(name = "waktu_selesai")
  private OffsetDateTime waktuSelesai;

  @Column(name = "waktu_dibatalkan")
  private OffsetDateTime waktuDibatalkan;

  @Column(name = "waktu_ditolak")
  private OffsetDateTime waktuDitolak;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diubah_oleh_terakhir")
  private User diubahOlehTerakhir;

  @PrePersist
  void prePersist() {
    if (waktuPermintaan == null) {
      waktuPermintaan = OffsetDateTime.now();
    }
  }
}
