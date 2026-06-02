package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.responses.UserProfileResponse;
import com.teknisio.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final AuthService authService;

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
    @RequestBody Map<String, String> request
  ) {
    UserProfileResponse response = authService.updateProfile(request);

    return ResponseEntity.ok(
      ApiResponse.success("Profile updated successfully", response)
    );
  }
}