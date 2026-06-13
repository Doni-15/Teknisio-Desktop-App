package com.teknisio.dto;

/**
 * Matches backend's AuthResponse record:
 * { accessToken, tokenType, expiresInMs, user: AuthUserResponse }
 */
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresInMs;
    private AuthUserResponse user;

    // Nested user object matching AuthUserResponse record
    public static class AuthUserResponse {
        private String userId;           // UUID as string
        private String technicianProfileId; // UUID as string, nullable
        private String name;
        private String email;
        private String phoneNumber;
        private String profilePhoto;     // base64 string
        private String address;
        private String role;             // "CUSTOMER" or "TECHNICIAN"
        private String accountStatus;

        public String getUserId() { return userId; }
        public String getTechnicianProfileId() { return technicianProfileId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getProfilePhoto() { return profilePhoto; }
        public String getAddress() { return address; }
        public String getRole() { return role; }
        public String getAccountStatus() { return accountStatus; }

        public void setUserId(String userId) { this.userId = userId; }
        public void setTechnicianProfileId(String technicianProfileId) { this.technicianProfileId = technicianProfileId; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
        public void setAddress(String address) { this.address = address; }
        public void setRole(String role) { this.role = role; }
        public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public Long getExpiresInMs() { return expiresInMs; }
    public AuthUserResponse getUser() { return user; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public void setExpiresInMs(Long expiresInMs) { this.expiresInMs = expiresInMs; }
    public void setUser(AuthUserResponse user) { this.user = user; }
}