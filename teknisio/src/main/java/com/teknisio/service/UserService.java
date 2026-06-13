package com.teknisio.service;

import com.teknisio.dto.AuthResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Service for user profile operations.
 * GET /api/auth/profile  — fetch current user profile
 * PUT /api/users/me      — update profile fields
 */
public class UserService {

    /**
     * Fetch current user's profile from backend and update SessionManager.
     * Call this after login to populate profilePhoto, technicianProfileId.
     */
    public static void refreshProfile() {
        try {
            ApiClient.ApiResponse<ProfileResponse> response =
                    ApiClient.get("/api/auth/profile", ProfileResponse.class);
            if (response.isSuccess() && response.getData() != null) {
                ProfileResponse profile = response.getData();
                SessionManager.setProfilePhoto(profile.profilePhoto);
                if (profile.technicianProfileId != null) {
                    SessionManager.setTechnicianProfileId(profile.technicianProfileId.toString());
                }
                // Also update session fields if changed
                if (profile.name != null) {
                    // re-login with updated fields
                    SessionManager.login(
                        SessionManager.getUserId(),
                        profile.email != null ? profile.email : SessionManager.getEmail(),
                        profile.name,
                        profile.phoneNumber != null ? profile.phoneNumber : SessionManager.getPhone(),
                        profile.address != null ? profile.address : SessionManager.getAddress(),
                        profile.role != null ? profile.role.toString() : SessionManager.getRole().name()
                    );
                    SessionManager.setProfilePhoto(profile.profilePhoto);
                    if (profile.technicianProfileId != null) {
                        SessionManager.setTechnicianProfileId(profile.technicianProfileId.toString());
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("UserService.refreshProfile error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Update user profile (name, phone, etc.) via PUT /api/users/me.
     * @param fields map of field names to new values (e.g. "name" -> "John")
     * @return true on success
     */
    public static boolean updateProfile(Map<String, String> fields) {
        try {
            ApiClient.ApiResponse<ProfileResponse> response =
                    ApiClient.put("/api/users/me", fields, ProfileResponse.class);
            if (response.isSuccess() && response.getData() != null) {
                ProfileResponse p = response.getData();
                // Refresh session with new data
                SessionManager.login(
                    SessionManager.getUserId(),
                    p.email != null ? p.email : SessionManager.getEmail(),
                    p.name != null ? p.name : SessionManager.getName(),
                    p.phoneNumber != null ? p.phoneNumber : SessionManager.getPhone(),
                    p.address != null ? p.address : SessionManager.getAddress(),
                    p.role != null ? p.role.toString() : SessionManager.getRole().name()
                );
                SessionManager.setProfilePhoto(p.profilePhoto);
                return true;
            }
            return false;
        } catch (IOException | InterruptedException e) {
            System.err.println("UserService.updateProfile error: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Inner POJO matching backend UserProfileResponse record.
     */
    public static class ProfileResponse {
        public Object userId;
        public Object technicianProfileId;
        public String name;
        public String email;
        public String phoneNumber;
        public String profilePhoto;
        public String address;
        public Object role;
        public Object accountStatus;
    }
}
