package com.teknisio.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CompleteServiceRequestRequest(
  @NotNull(message = "Final cost is required")
  @DecimalMin(value = "0.00", message = "Final cost must be greater than or equal to 0")
  BigDecimal finalCost,

  @Size(max = 1000, message = "Technician note must be at most 1000 characters")
  String technicianNote
) {
}
