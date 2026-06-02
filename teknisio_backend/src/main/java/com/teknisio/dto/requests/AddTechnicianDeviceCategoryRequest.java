package com.teknisio.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record AddTechnicianDeviceCategoryRequest(
  @NotBlank(message = "Device category id is required")
  String deviceCategoryId
) {
}
