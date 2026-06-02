package com.teknisio.dto.responses;

import com.teknisio.model.enums.RequestStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceRequestResponse(
  UUID serviceRequestId,
  String serviceRequestCode,
  UUID customerId,
  UUID technicianProfileId,
  RequestStatus status,
  String issueDescription,
  String address,
  String addressDetail,
  String cancelReason,
  List<DeviceCategoryResponse> selectedDeviceCategories,
  OffsetDateTime requestTime,
  OffsetDateTime cancelledAt
) {
}
