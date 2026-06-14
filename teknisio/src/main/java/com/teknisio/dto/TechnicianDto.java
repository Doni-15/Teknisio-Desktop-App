package com.teknisio.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Matches backend CustomerTechnicianResponse record.
 * profilePhoto is a base64-encoded string.
 */
public class TechnicianDto {
    private String technicianProfileId;
    private String name;
    private String profilePhoto; // base64 string
    private String availabilityStatus; // "AVAILABLE", "BUSY", "OFFLINE"
    private BigDecimal averageRating;
    private Integer ratingCount;
    private Integer totalJobs;
    private String description;
    private List<DeviceCategoryDto> supportedDeviceCategories;
    private String phoneNumber;

    public TechnicianDto() {}

    public String getTechnicianProfileId() { return technicianProfileId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getName() { return name; }
    public String getProfilePhoto() { return profilePhoto; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public BigDecimal getAverageRating() { return averageRating; }
    public Integer getRatingCount() { return ratingCount; }
    public Integer getTotalJobs() { return totalJobs; }
    public String getDescription() { return description; }
    public List<DeviceCategoryDto> getSupportedDeviceCategories() { return supportedDeviceCategories; }

    public void setTechnicianProfileId(String id) { this.technicianProfileId = id; }
    public void setName(String name) { this.name = name; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public void setAvailabilityStatus(String status) { this.availabilityStatus = status; }
    public void setAverageRating(BigDecimal rating) { this.averageRating = rating; }
    public void setRatingCount(Integer count) { this.ratingCount = count; }
    public void setTotalJobs(Integer jobs) { this.totalJobs = jobs; }
    public void setDescription(String description) { this.description = description; }
    public void setSupportedDeviceCategories(List<DeviceCategoryDto> cats) { this.supportedDeviceCategories = cats; }

    /** Convenience: get rating as double (0.0 if null) */
    public double getRatingDouble() {
        return averageRating != null ? averageRating.doubleValue() : 0.0;
    }

    /** Convenience: check if technician is available */
    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(availabilityStatus);
    }
}
