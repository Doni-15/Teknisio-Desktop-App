package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseCreatedAtEntity;
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
@Table(name = "permintaan_layanan_kategori")
public class PermintaanLayananKategori extends BaseCreatedAtEntity {

  @EmbeddedId
  private PermintaanLayananKategoriId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("idPermintaan")
  @JoinColumn(name = "id_permintaan", nullable = false)
  private PermintaanLayanan permintaan;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("idKategori")
  @JoinColumn(name = "id_kategori", nullable = false)
  private KategoriLayanan kategori;
}
