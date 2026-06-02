package com.teknisio.dto.requests;

import jakarta.validation.constraints.Size;

public record RejectServiceRequestRequest(
  @Size(max = 1000, message = "Reject reason must be at most 1000 characters")
  String rejectReason
) {
}
