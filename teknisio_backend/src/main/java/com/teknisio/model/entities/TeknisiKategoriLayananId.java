package com.teknisio.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TeknisiKategoriLayananId implements Serializable {

  @Column(name = "id_teknisi_profile")
  private UUID idTeknisiProfile;

  @Column(name = "id_kategori")
  private UUID idKategori;
}
