package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.LoginRequest;
import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.requests.RegisterTechnicianRequest;
import com.teknisio.dto.responses.AuthResponse;
import com.teknisio.dto.responses.UserProfileResponse;
import com.teknisio.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register/customer")
  public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(
    @Valid @RequestBody RegisterCustomerRequest request
  ) {
    AuthResponse response = authService.registerCustomer(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Customer registered successfully", response));
  }

  @PostMapping("/register/technician")
  public ResponseEntity<ApiResponse<AuthResponse>> registerTechnician(
    @Valid @RequestBody RegisterTechnicianRequest request
  ) {
    AuthResponse response = authService.registerTechnician(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Technician registered successfully", response));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
    @Valid @RequestBody LoginRequest request
  ) {
    AuthResponse response = authService.login(request);

    return ResponseEntity.ok(
      ApiResponse.success("Login successful", response)
    );
  }

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
    UserProfileResponse response = authService.getProfile();

    return ResponseEntity.ok(
      ApiResponse.success("Profile retrieved successfully", response)
    );
  }
}
