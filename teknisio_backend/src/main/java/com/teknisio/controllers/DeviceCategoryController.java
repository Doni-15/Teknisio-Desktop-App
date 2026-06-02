package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.services.DeviceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/device-categories")
@RequiredArgsConstructor
public class DeviceCategoryController {

  private final DeviceCategoryService deviceCategoryService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<DeviceCategoryResponse>>> getDeviceCategories() {
    List<DeviceCategoryResponse> response = deviceCategoryService.getActiveDeviceCategories();

    return ResponseEntity.ok(
      ApiResponse.success("Device categories retrieved successfully", response)
    );
  }

  @GetMapping("/{deviceCategoryId}")
  public ResponseEntity<ApiResponse<DeviceCategoryResponse>> getDeviceCategoryById(
    @PathVariable String deviceCategoryId
  ) {
    DeviceCategoryResponse response = deviceCategoryService.getActiveDeviceCategoryById(deviceCategoryId);

    return ResponseEntity.ok(
      ApiResponse.success("Device category retrieved successfully", response)
    );
  }
}
