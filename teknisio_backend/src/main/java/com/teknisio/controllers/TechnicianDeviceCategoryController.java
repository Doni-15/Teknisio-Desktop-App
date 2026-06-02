package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.AddTechnicianDeviceCategoryRequest;
import com.teknisio.dto.responses.TechnicianDeviceCategoryResponse;
import com.teknisio.services.TechnicianDeviceCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/technicians/device-categories")
public class TechnicianDeviceCategoryController {

  private final TechnicianDeviceCategoryService technicianDeviceCategoryService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TechnicianDeviceCategoryResponse>>> getMyDeviceCategories() {
    List<TechnicianDeviceCategoryResponse> response =
      technicianDeviceCategoryService.getMyDeviceCategories();

    return ResponseEntity.ok(
      ApiResponse.success("Technician device categories retrieved successfully", response)
    );
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TechnicianDeviceCategoryResponse>> addDeviceCategory(
    @Valid @RequestBody AddTechnicianDeviceCategoryRequest request
  ) {
    TechnicianDeviceCategoryResponse response =
      technicianDeviceCategoryService.addDeviceCategory(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Technician device category added successfully", response));
  }

  @DeleteMapping("/{deviceCategoryId}")
  public ResponseEntity<ApiResponse<Void>> removeDeviceCategory(
    @PathVariable String deviceCategoryId
  ) {
    technicianDeviceCategoryService.removeDeviceCategory(deviceCategoryId);

    return ResponseEntity.ok(
      ApiResponse.success("Technician device category removed successfully")
    );
  }
}
