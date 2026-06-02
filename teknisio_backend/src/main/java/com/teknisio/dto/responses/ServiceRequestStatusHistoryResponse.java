package com.teknisio.dto.responses;

import com.teknisio.model.enums.RequestStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ServiceRequestStatusHistoryResponse(
  UUID statusHistoryId,
  RequestStatus previousStatus,
  RequestStatus newStatus,
  String note,
  UUID changedByUserId,
  OffsetDateTime changedAt
) {
}