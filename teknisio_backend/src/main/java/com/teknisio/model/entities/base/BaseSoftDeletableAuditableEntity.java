package com.teknisio.model.entities.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseSoftDeletableAuditableEntity extends BaseAuditableEntity {

  @Column(name = "deleted_at")
  private OffsetDateTime deletedAt;
}
