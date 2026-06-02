package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.responses.CustomerTechnicianResponse;
import com.teknisio.services.CustomerTechnicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers/technicians")
public class CustomerTechnicianController {

  private final CustomerTechnicianService customerTechnicianService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CustomerTechnicianResponse>>> searchTechnicians(
    @RequestParam(required = false) String deviceCategoryId,
    @RequestParam(required = false) String availabilityStatus,
    @RequestParam(required = false) String sort
  ) {
    List<CustomerTechnicianResponse> response = customerTechnicianService.searchTechnicians(
      deviceCategoryId,
      availabilityStatus,
      sort
    );

    return ResponseEntity.ok(
      ApiResponse.success("Technicians retrieved successfully", response)
    );
  }

  @GetMapping("/{technicianProfileId}")
  public ResponseEntity<ApiResponse<CustomerTechnicianResponse>> getTechnicianDetail(
    @PathVariable String technicianProfileId
  ) {
    CustomerTechnicianResponse response =
      customerTechnicianService.getTechnicianDetail(technicianProfileId);

    return ResponseEntity.ok(
      ApiResponse.success("Technician retrieved successfully", response)
    );
  }
}
