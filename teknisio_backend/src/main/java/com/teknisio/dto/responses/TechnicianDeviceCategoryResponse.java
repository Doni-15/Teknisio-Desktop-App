package com.teknisio.dto.responses;

import java.util.UUID;

public record TechnicianDeviceCategoryResponse(
  UUID technicianProfileId,
  UUID deviceCategoryId,
  String name,
  String icon,
  Boolean active
) {
}
