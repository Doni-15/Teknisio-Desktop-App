package com.teknisio.service;

import com.google.gson.reflect.TypeToken;
import com.teknisio.dto.DeviceCategoryDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching device categories from backend.
 * GET /api/device-categories
 */
public class DeviceCategoryService {

    private static final Type LIST_TYPE = new TypeToken<List<DeviceCategoryDto>>() {}.getType();

    /**
     * Fetch all active device categories from backend.
     * @return list of DeviceCategoryDto, empty list on error
     */
    public static List<DeviceCategoryDto> getActiveCategories() {
        try {
            ApiClient.ApiResponse<List<DeviceCategoryDto>> response =
                    ApiClient.get("/api/device-categories", LIST_TYPE);
            if (response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("DeviceCategoryService.getActiveCategories error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>();
    }
}
