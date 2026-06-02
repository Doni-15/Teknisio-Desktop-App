package com.teknisio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateServiceRequestRequest(
  @NotBlank(message = "Technician profile id is required")
  String technicianProfileId,

  @NotEmpty(message = "Device category ids are required")
  @Size(max = 10, message = "Device category ids must be at most 10 items")
  List<@NotBlank(message = "Device category id cannot be blank") String> deviceCategoryIds,

  @NotBlank(message = "Issue description is required")
  @Size(max = 1000, message = "Issue description must be at most 1000 characters")
  String issueDescription,

  @NotBlank(message = "Address is required")
  @Size(max = 500, message = "Address must be at most 500 characters")
  String address,

  @Size(max = 500, message = "Address detail must be at most 500 characters")
  String addressDetail
) {
}
