package com.teknisio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelServiceRequestRequest(
  @NotBlank(message = "Cancel reason is required")
  @Size(max = 1000, message = "Cancel reason must be at most 1000 characters")
  String cancelReason
) {
}
