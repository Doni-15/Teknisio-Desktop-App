package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teknisi_kategori_layanan")
public class TeknisiKategoriLayanan extends BaseAuditableEntity {

  @EmbeddedId
  private TeknisiKategoriLayananId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("idTeknisiProfile")
  @JoinColumn(name = "id_teknisi_profile", nullable = false)
  private TeknisiProfile teknisiProfile;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("idKategori")
  @JoinColumn(name = "id_kategori", nullable = false)
  private KategoriLayanan kategori;

  @Column(name = "aktif", nullable = false)
  @Builder.Default
  private Boolean aktif = true;
}
