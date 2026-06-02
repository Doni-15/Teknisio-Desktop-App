package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseSoftDeletableAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kategori_layanan")
public class KategoriLayanan extends BaseSoftDeletableAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_kategori", nullable = false, updatable = false)
  private UUID idKategori;

  @Column(name = "nama_kategori", nullable = false, length = 100)
  private String namaKategori;

  @Column(name = "icon", columnDefinition = "TEXT")
  private String icon;

  @Column(name = "aktif", nullable = false)
  @Builder.Default
  private Boolean aktif = true;
}
