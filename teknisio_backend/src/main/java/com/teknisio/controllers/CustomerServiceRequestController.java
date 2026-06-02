package com.teknisio.controllers;

import com.teknisio.common.response.ApiResponse;
import com.teknisio.dto.requests.CancelServiceRequestRequest;
import com.teknisio.dto.requests.CreateServiceRequestRequest;
import com.teknisio.dto.responses.ServiceRequestResponse;
import com.teknisio.dto.requests.CreateReviewRequest;
import com.teknisio.dto.responses.ReviewResponse;
import com.teknisio.services.CustomerServiceRequestService;
import com.teknisio.dto.responses.ServiceRequestStatusHistoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers/service-requests")
public class CustomerServiceRequestController {

  private final CustomerServiceRequestService customerServiceRequestService;

  @PostMapping
  public ResponseEntity<ApiResponse<ServiceRequestResponse>> createServiceRequest(
    @Valid @RequestBody CreateServiceRequestRequest request
  ) {
    ServiceRequestResponse response = customerServiceRequestService.createServiceRequest(request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Service request created successfully", response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ServiceRequestResponse>>> getMyServiceRequests(
    @RequestParam(required = false) String status
  ) {
    List<ServiceRequestResponse> response = customerServiceRequestService.getMyServiceRequests(status);

    return ResponseEntity.ok(
      ApiResponse.success("Service requests retrieved successfully", response)
    );
  }

  @GetMapping("/{serviceRequestId}")
  public ResponseEntity<ApiResponse<ServiceRequestResponse>> getMyServiceRequestDetail(
    @PathVariable String serviceRequestId
  ) {
    ServiceRequestResponse response = customerServiceRequestService.getMyServiceRequestDetail(serviceRequestId);

    return ResponseEntity.ok(
      ApiResponse.success("Service request retrieved successfully", response)
    );
  }

  @GetMapping("/{serviceRequestId}/status-history")
  public ResponseEntity<ApiResponse<List<ServiceRequestStatusHistoryResponse>>> getMyServiceRequestStatusHistory(
    @PathVariable String serviceRequestId
  ) {
    List<ServiceRequestStatusHistoryResponse> response =
      customerServiceRequestService.getMyServiceRequestStatusHistory(serviceRequestId);
  
    return ResponseEntity.ok(
      ApiResponse.success("Service request status history retrieved successfully", response)
    );
  }

  @PatchMapping("/{serviceRequestId}/cancel")
  public ResponseEntity<ApiResponse<ServiceRequestResponse>> cancelMyServiceRequest(
    @PathVariable String serviceRequestId,
    @Valid @RequestBody CancelServiceRequestRequest request
  ) {
    ServiceRequestResponse response = customerServiceRequestService.cancelMyServiceRequest(
      serviceRequestId,
      request
    );

    return ResponseEntity.ok(
      ApiResponse.success("Service request cancelled successfully", response)
    );
  }

  @PostMapping("/{serviceRequestId}/review")
  public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
    @PathVariable String serviceRequestId,
    @Valid @RequestBody CreateReviewRequest request
  ) {
    ReviewResponse response = customerServiceRequestService.createReview(
      serviceRequestId,
      request
    );

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ApiResponse.success("Review created successfully", response));
  }
}
