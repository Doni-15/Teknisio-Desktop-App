package com.teknisio.dto;

import java.util.List;

/**
 * Request body for POST /api/customers/service-requests
 */
public class CreateServiceRequestDto {
    private String technicianProfileId;
    private List<String> deviceCategoryIds;
    private String issueDescription;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;

    public CreateServiceRequestDto() {}

    public CreateServiceRequestDto(String technicianProfileId, List<String> deviceCategoryIds,
                                   String issueDescription, String address, String addressDetail,
                                   Double latitude, Double longitude) {
        this.technicianProfileId = technicianProfileId;
        this.deviceCategoryIds = deviceCategoryIds;
        this.issueDescription = issueDescription;
        this.address = address;
        this.addressDetail = addressDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTechnicianProfileId() { return technicianProfileId; }
    public List<String> getDeviceCategoryIds() { return deviceCategoryIds; }
    public String getIssueDescription() { return issueDescription; }
    public String getAddress() { return address; }
    public String getAddressDetail() { return addressDetail; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setTechnicianProfileId(String id) { this.technicianProfileId = id; }
    public void setDeviceCategoryIds(List<String> ids) { this.deviceCategoryIds = ids; }
    public void setIssueDescription(String desc) { this.issueDescription = desc; }
    public void setAddress(String address) { this.address = address; }
    public void setAddressDetail(String detail) { this.addressDetail = detail; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
