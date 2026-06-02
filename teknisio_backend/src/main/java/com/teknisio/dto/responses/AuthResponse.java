package com.teknisio.dto.responses;

public record AuthResponse(
  String accessToken,
  String tokenType,
  Long expiresInMs,
  AuthUserResponse user
) {
}
