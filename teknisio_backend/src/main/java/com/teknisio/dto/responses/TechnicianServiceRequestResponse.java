package com.teknisio.dto.responses;

import com.teknisio.model.enums.RequestStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TechnicianServiceRequestResponse(
  UUID serviceRequestId,
  String serviceRequestCode,

  UUID customerId,
  String customerName,
  String customerPhoneNumber,
  String customerProfilePhoto,

  UUID technicianProfileId,
  RequestStatus status,

  String issueDescription,
  String address,
  String addressDetail,
  BigDecimal latitude,
  BigDecimal longitude,

  BigDecimal estimatedCost,
  BigDecimal finalCost,
  String technicianNote,

  String cancelReason,
  String rejectReason,

  List<DeviceCategoryResponse> selectedDeviceCategories,

  OffsetDateTime requestTime,
  OffsetDateTime acceptedAt,
  OffsetDateTime startedAt,
  OffsetDateTime completedAt,
  OffsetDateTime cancelledAt,
  OffsetDateTime rejectedAt
) {
}
