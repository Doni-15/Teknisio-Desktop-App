package com.teknisio.service;

import com.google.gson.reflect.TypeToken;
import com.teknisio.dto.CreateServiceRequestDto;
import com.teknisio.dto.ServiceRequestDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for customer service request operations.
 */
public class ServiceRequestService {

    private static final Type LIST_TYPE = new TypeToken<List<ServiceRequestDto>>() {}.getType();

    /**
     * Create a new service request.
     * POST /api/customers/service-requests
     */
    public static ServiceRequestDto createServiceRequest(CreateServiceRequestDto request) throws IOException, InterruptedException {
        ApiClient.ApiResponse<ServiceRequestDto> response =
                ApiClient.post("/api/customers/service-requests", request, ServiceRequestDto.class);
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new IOException("Gagal membuat order: " + response.getMessage());
    }

    /**
     * Get logged-in customer's service requests.
     * GET /api/customers/service-requests
     */
    public static List<ServiceRequestDto> getMyServiceRequests(String status) {
        try {
            StringBuilder path = new StringBuilder("/api/customers/service-requests");
            if (status != null && !status.isBlank()) {
                path.append("?status=").append(status);
            }
            ApiClient.ApiResponse<List<ServiceRequestDto>> response =
                    ApiClient.get(path.toString(), LIST_TYPE);
            if (response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ServiceRequestService.getMyServiceRequests error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>();
    }

    /**
     * Get detail of a single service request.
     * GET /api/customers/service-requests/{id}
     */
    public static ServiceRequestDto getServiceRequestDetail(String serviceRequestId) {
        try {
            ApiClient.ApiResponse<ServiceRequestDto> response =
                    ApiClient.get("/api/customers/service-requests/" + serviceRequestId, ServiceRequestDto.class);
            if (response.isSuccess()) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ServiceRequestService.getServiceRequestDetail error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Cancel a service request.
     * PATCH /api/customers/service-requests/{id}/cancel
     */
    public static ServiceRequestDto cancelServiceRequest(String serviceRequestId, String cancelReason) throws IOException, InterruptedException {
        java.util.Map<String, String> body = java.util.Map.of("cancelReason", cancelReason);
        ApiClient.ApiResponse<ServiceRequestDto> response =
                ApiClient.patch("/api/customers/service-requests/" + serviceRequestId + "/cancel", body, ServiceRequestDto.class);
        if (response.isSuccess()) {
            return response.getData();
        }
        throw new IOException(response.getMessage());
    }

    /**
     * Create a review for a completed service request.
     * POST /api/customers/service-requests/{id}/review
     */
    public static boolean createReview(String serviceRequestId, int rating, String comment) throws IOException, InterruptedException {
        java.util.Map<String, Object> body = java.util.Map.of("rating", rating, "comment", comment);
        ApiClient.ApiResponse<Object> response =
                ApiClient.post("/api/customers/service-requests/" + serviceRequestId + "/review", body, Object.class);
        return response.isSuccess();
    }

}
