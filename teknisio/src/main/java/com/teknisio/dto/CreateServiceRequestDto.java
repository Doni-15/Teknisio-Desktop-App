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

    public CreateServiceRequestDto() {}

    public CreateServiceRequestDto(String technicianProfileId, List<String> deviceCategoryIds,
                                   String issueDescription, String address, String addressDetail) {
        this.technicianProfileId = technicianProfileId;
        this.deviceCategoryIds = deviceCategoryIds;
        this.issueDescription = issueDescription;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public String getTechnicianProfileId() { return technicianProfileId; }
    public List<String> getDeviceCategoryIds() { return deviceCategoryIds; }
    public String getIssueDescription() { return issueDescription; }
    public String getAddress() { return address; }
    public String getAddressDetail() { return addressDetail; }

    public void setTechnicianProfileId(String id) { this.technicianProfileId = id; }
    public void setDeviceCategoryIds(List<String> ids) { this.deviceCategoryIds = ids; }
    public void setIssueDescription(String desc) { this.issueDescription = desc; }
    public void setAddress(String address) { this.address = address; }
    public void setAddressDetail(String detail) { this.addressDetail = detail; }
}
