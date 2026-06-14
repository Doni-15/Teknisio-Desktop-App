package com.teknisio.service;

import com.google.gson.reflect.TypeToken;
import com.teknisio.dto.TechnicianDto;
import com.teknisio.dto.DeviceCategoryDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching technician data from backend.
 * GET /api/customers/technicians
 * GET /api/customers/technicians/{id}
 */
public class TechnicianService {

    private static final Type LIST_TYPE = new TypeToken<List<TechnicianDto>>() {}.getType();

    /**
     * Search/list technicians, optionally filtered by device category.
     * @param deviceCategoryId UUID string, or null for all
     * @return list of TechnicianDto (empty on error)
     */
    public static List<TechnicianDto> searchTechnicians(String deviceCategoryId) {
        try {
            StringBuilder path = new StringBuilder("/api/customers/technicians");
            if (deviceCategoryId != null && !deviceCategoryId.isBlank()) {
                path.append("?deviceCategoryId=").append(deviceCategoryId);
            }
            ApiClient.ApiResponse<List<TechnicianDto>> response =
                    ApiClient.get(path.toString(), LIST_TYPE);
            if (response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianService.searchTechnicians error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>();
    }

    /**
     * Get detail of a single technician by profile ID.
     * @param technicianProfileId UUID string
     * @return TechnicianDto or null on error
     */
    public static TechnicianDto getTechnicianDetail(String technicianProfileId) {
        try {
            ApiClient.ApiResponse<TechnicianDto> response =
                    ApiClient.get("/api/customers/technicians/" + technicianProfileId, TechnicianDto.class);
            if (response.isSuccess()) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianService.getTechnicianDetail error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Get device categories handled by the logged-in technician.
     * GET /api/technicians/device-categories
     */
    public static List<DeviceCategoryDto> getMyDeviceCategories() {
        try {
            Type listType = new TypeToken<List<DeviceCategoryDto>>() {}.getType();
            ApiClient.ApiResponse<List<DeviceCategoryDto>> response =
                    ApiClient.get("/api/technicians/device-categories", listType);
            if (response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianService.getMyDeviceCategories error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>();
    }

    /**
     * Add a device category to the logged-in technician's skills.
     * POST /api/technicians/device-categories
     */
    public static boolean addDeviceCategory(String deviceCategoryId) {
        try {
            java.util.Map<String, String> body = java.util.Map.of("deviceCategoryId", deviceCategoryId);
            ApiClient.ApiResponse<Object> response =
                    ApiClient.post("/api/technicians/device-categories", body, Object.class);
            return response.isSuccess();
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianService.addDeviceCategory error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return false;
    }

    /**
     * Remove a device category from the logged-in technician's skills.
     * DELETE /api/technicians/device-categories/{deviceCategoryId}
     */
    public static boolean removeDeviceCategory(String deviceCategoryId) {
        try {
            ApiClient.ApiResponse<Object> response =
                    ApiClient.delete("/api/technicians/device-categories/" + deviceCategoryId, Object.class);
            return response.isSuccess();
        } catch (IOException | InterruptedException e) {
            System.err.println("TechnicianService.removeDeviceCategory error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return false;
    }

}
