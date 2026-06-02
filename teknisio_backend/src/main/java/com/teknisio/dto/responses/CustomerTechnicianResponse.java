package com.teknisio.dto.responses;

import com.teknisio.model.enums.TeknisiStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CustomerTechnicianResponse(
  UUID technicianProfileId,
  String name,
  String profilePhoto,
  TeknisiStatus availabilityStatus,
  BigDecimal averageRating,
  Integer ratingCount,
  Integer totalJobs,
  String description,
  List<DeviceCategoryResponse> supportedDeviceCategories
) {
}
