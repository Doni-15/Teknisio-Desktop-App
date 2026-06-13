package com.teknisio.service;

/**
 * Manages the current authenticated user's session.
 */
public class SessionManager {

    public enum UserRole {
        CUSTOMER,
        TECHNICIAN
    }

    private static Long userId;
    private static String email;
    private static String name;
    private static String phone;
    private static String address;
    private static UserRole role;

    public static void login(Long userId, String email, String name, String phone, String address, String roleStr) {
        SessionManager.userId = userId;
        SessionManager.email = email;
        SessionManager.name = name;
        SessionManager.phone = phone;
        SessionManager.address = address;
        try {
            SessionManager.role = UserRole.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            SessionManager.role = UserRole.CUSTOMER;
        }
    }

    public static void logout() {
        userId = null;
        email = null;
        name = null;
        phone = null;
        address = null;
        role = null;
        ApiClient.clearToken();
    }

    public static boolean isLoggedIn() {
        return userId != null && ApiClient.getToken() != null;
    }

    public static Long getUserId() { return userId; }
    public static String getEmail() { return email; }
    public static String getName() { return name; }
    public static String getPhone() { return phone; }
    public static String getAddress() { return address; }
    public static UserRole getRole() { return role; }

    public static boolean isTechnician() { return role == UserRole.TECHNICIAN; }
    public static boolean isCustomer() { return role == UserRole.CUSTOMER; }
}