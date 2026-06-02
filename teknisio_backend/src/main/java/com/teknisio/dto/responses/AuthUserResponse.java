package com.teknisio.dto.responses;

import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.util.UUID;

public record AuthUserResponse(
  UUID userId,
  UUID technicianProfileId,
  String name,
  String email,
  String phoneNumber,
  String profilePhoto,
  String address,
  UserRole role,
  UserStatus accountStatus
) {
}
