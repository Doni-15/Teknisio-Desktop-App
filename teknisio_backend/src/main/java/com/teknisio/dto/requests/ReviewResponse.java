package com.teknisio.dto.responses;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewResponse(
  UUID reviewId,
  UUID serviceRequestId,
  UUID customerId,
  UUID technicianProfileId,
  Integer rating,
  String comment,
  OffsetDateTime createdAt,
  OffsetDateTime updatedAt
) {
}