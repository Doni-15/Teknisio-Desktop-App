package com.teknisio.service;

import com.google.gson.reflect.TypeToken;
import com.teknisio.dto.ServiceRequestDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for technician-side service request operations.
 * GET /api/technicians/service-requests
 * PATCH /api/technicians/service-requests/{id}/accept
 * PATCH /api/technicians/service-requests/{id}/reject
 * PATCH /api/technicians/service-requests/{id}/start
 * PATCH /api/technicians/service-requests/{id}/complete
 */
public class TechnicianRequestService {

    private static final Type LIST_TYPE = new TypeToken<List<ServiceRequestDto>>() {}.getType();

    /**
     * Get technician's service requests, optionally filtered by status.
     */
    public static List<ServiceRequestDto> getMyRequests(String status) {
        try {
            StringBuilder path = new StringBuilder("/api/technicians/service-requests");
            if (status != null && !status.isBlank()) {
                path.append("?status=").append(status);
            }
            ApiClient.ApiResponse<List<ServiceRequestDto>> response =
                    ApiClient.get(path.toString(), LIST_TYPE);
            if (response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianRequestService.getMyRequests error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>();
    }

    /**
     * Get single service request detail for technician.
     */
    public static ServiceRequestDto getRequestDetail(String serviceRequestId) {
        try {
            ApiClient.ApiResponse<ServiceRequestDto> response =
                    ApiClient.get("/api/technicians/service-requests/" + serviceRequestId, ServiceRequestDto.class);
            if (response.isSuccess()) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianRequestService.getRequestDetail error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Accept a pending service request.
     */
    public static ServiceRequestDto acceptRequest(String serviceRequestId) throws IOException, InterruptedException {
        ApiClient.ApiResponse<ServiceRequestDto> response =
                ApiClient.patch("/api/technicians/service-requests/" + serviceRequestId + "/accept", null, ServiceRequestDto.class);
        if (response.isSuccess()) return response.getData();
        throw new IOException(response.getMessage());
    }

    /**
     * Reject a service request with optional reason.
     */
    public static ServiceRequestDto rejectRequest(String serviceRequestId, String reason) throws IOException, InterruptedException {
        Map<String, String> body = reason != null ? Map.of("rejectReason", reason) : null;
        ApiClient.ApiResponse<ServiceRequestDto> response =
                ApiClient.patch("/api/technicians/service-requests/" + serviceRequestId + "/reject", body, ServiceRequestDto.class);
        if (response.isSuccess()) return response.getData();
        throw new IOException(response.getMessage());
    }

    /**
     * Start working on an accepted service request.
     */
    public static ServiceRequestDto startRequest(String serviceRequestId) throws IOException, InterruptedException {
        ApiClient.ApiResponse<ServiceRequestDto> response =
                ApiClient.patch("/api/technicians/service-requests/" + serviceRequestId + "/start", null, ServiceRequestDto.class);
        if (response.isSuccess()) return response.getData();
        throw new IOException(response.getMessage());
    }

    /**
     * Complete a service request with technician note and final cost.
     */
    public static ServiceRequestDto completeRequest(String serviceRequestId, String note, String finalCost) throws IOException, InterruptedException {
        Map<String, String> body = note != null
                ? Map.of("technicianNote", note, "finalCost", finalCost != null ? finalCost : "0")
                : Map.of("finalCost", finalCost != null ? finalCost : "0");
        ApiClient.ApiResponse<ServiceRequestDto> response =
                ApiClient.patch("/api/technicians/service-requests/" + serviceRequestId + "/complete", body, ServiceRequestDto.class);
        if (response.isSuccess()) return response.getData();
        throw new IOException(response.getMessage());
    }

    /**
     * Update technician's current location for a service request.
     */
    public static boolean updateRequestLocation(String serviceRequestId, double latitude, double longitude) {
        try {
            String path = "/api/technicians/service-requests/" + serviceRequestId + "/location"
                    + "?latitude=" + latitude + "&longitude=" + longitude;
            ApiClient.ApiResponse<Void> response =
                    ApiClient.patch(path, null, Void.class);
            return response.isSuccess();
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianRequestService.updateRequestLocation error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
