package com.teknisio.model.entities;

import com.teknisio.model.entities.base.BaseCreatedAtEntity;
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

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pesan_chat")
public class PesanChat extends BaseCreatedAtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id_pesan", nullable = false, updatable = false)
  private UUID idPesan;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_permintaan", nullable = false)
  private PermintaanLayanan permintaanLayanan;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_pengirim", nullable = false)
  private User pengirim;

  @Column(name = "isi", nullable = false, columnDefinition = "TEXT")
  private String isi;
}
