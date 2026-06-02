package com.teknisio.dto.responses;

import java.util.UUID;

public record DeviceCategoryResponse(
  UUID deviceCategoryId,
  String name,
  String icon
) {
}
