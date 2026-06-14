package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.CompleteServiceRequestRequest;
import com.teknisio.dto.requests.RejectServiceRequestRequest;
import com.teknisio.dto.responses.TechnicianServiceRequestResponse;
import com.teknisio.services.TechnicianServiceRequestService;
import com.teknisio.dto.responses.ServiceRequestStatusHistoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/technicians/service-requests")
public class TechnicianServiceRequestController {

  private final TechnicianServiceRequestService technicianServiceRequestService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TechnicianServiceRequestResponse>>> getMyServiceRequests(
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String sort
  ) {
    List<TechnicianServiceRequestResponse> response =
      technicianServiceRequestService.getMyServiceRequests(status, sort);

    return ResponseEntity.ok(
      ApiResponse.success("Service requests retrieved successfully", response)
    );
  }

  @GetMapping("/{serviceRequestId}")
  public ResponseEntity<ApiResponse<TechnicianServiceRequestResponse>> getMyServiceRequestDetail(
    @PathVariable String serviceRequestId
  ) {
    TechnicianServiceRequestResponse response =
      technicianServiceRequestService.getMyServiceRequestDetail(serviceRequestId);

    return ResponseEntity.ok(
      ApiResponse.success("Service request retrieved successfully", response)
    );
  }

  @GetMapping("/{serviceRequestId}/status-history")
  public ResponseEntity<ApiResponse<List<ServiceRequestStatusHistoryResponse>>> getMyServiceRequestStatusHistory(
    @PathVariable String serviceRequestId
  ) {
    List<ServiceRequestStatusHistoryResponse> response =
      technicianServiceRequestService.getMyServiceRequestStatusHistory(serviceRequestId);
  
    return ResponseEntity.ok(
      ApiResponse.success("Service request status history retrieved successfully", response)
    );
  }

  @PatchMapping("/{serviceRequestId}/accept")
  public ResponseEntity<ApiResponse<TechnicianServiceRequestResponse>> acceptServiceRequest(
    @PathVariable String serviceRequestId
  ) {
    TechnicianServiceRequestResponse response =
      technicianServiceRequestService.acceptMyServiceRequest(serviceRequestId);

    return ResponseEntity.ok(
      ApiResponse.success("Service request accepted successfully", response)
    );
  }

  @PatchMapping("/{serviceRequestId}/reject")
  public ResponseEntity<ApiResponse<TechnicianServiceRequestResponse>> rejectServiceRequest(
    @PathVariable String serviceRequestId,
    @Valid @RequestBody(required = false) RejectServiceRequestRequest request
  ) {
    TechnicianServiceRequestResponse response =
      technicianServiceRequestService.rejectMyServiceRequest(serviceRequestId, request);

    return ResponseEntity.ok(
      ApiResponse.success("Service request rejected successfully", response)
    );
  }

  @PatchMapping("/{serviceRequestId}/start")
  public ResponseEntity<ApiResponse<TechnicianServiceRequestResponse>> startServiceRequest(
    @PathVariable String serviceRequestId
  ) {
    TechnicianServiceRequestResponse response =
      technicianServiceRequestService.startMyServiceRequest(serviceRequestId);

    return ResponseEntity.ok(
      ApiResponse.success("Service request started successfully", response)
    );
  }

  @PatchMapping("/{serviceRequestId}/complete")
  public ResponseEntity<ApiResponse<TechnicianServiceRequestResponse>> completeServiceRequest(
    @PathVariable String serviceRequestId,
    @Valid @RequestBody CompleteServiceRequestRequest request
  ) {
    TechnicianServiceRequestResponse response =
      technicianServiceRequestService.completeMyServiceRequest(serviceRequestId, request);

    return ResponseEntity.ok(
      ApiResponse.success("Service request completed successfully", response)
    );
  }

  @PatchMapping("/{serviceRequestId}/location")
  public ResponseEntity<ApiResponse<Void>> updateServiceRequestLocation(
    @PathVariable String serviceRequestId,
    @RequestParam java.math.BigDecimal latitude,
    @RequestParam java.math.BigDecimal longitude
  ) {
    technicianServiceRequestService.updateServiceRequestLocation(serviceRequestId, latitude, longitude);
    return ResponseEntity.ok(
      ApiResponse.success("Location updated successfully", null)
    );
  }
}
