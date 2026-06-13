package com.teknisio.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Matches backend ServiceRequestResponse record (customer side).
 */
public class ServiceRequestDto {
    private String serviceRequestId;
    private String serviceRequestCode;
    private String customerId;
    private String customerName;
    private String customerPhoneNumber;
    private String customerProfilePhoto; // base64
    private String technicianProfileId;
    private String status;
    private String issueDescription;
    private String address;
    private String addressDetail;
    private BigDecimal estimatedCost;
    private BigDecimal finalCost;
    private String technicianNote;
    private String cancelReason;
    private String rejectReason;
    private List<DeviceCategoryDto> selectedDeviceCategories;
    private String requestTime;
    private String acceptedAt;
    private String startedAt;
    private String completedAt;
    private String cancelledAt;
    private String rejectedAt;

    public ServiceRequestDto() {}

    public String getServiceRequestId() { return serviceRequestId; }
    public String getServiceRequestCode() { return serviceRequestCode; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhoneNumber() { return customerPhoneNumber; }
    public String getCustomerProfilePhoto() { return customerProfilePhoto; }
    public String getTechnicianProfileId() { return technicianProfileId; }
    public String getStatus() { return status; }
    public String getIssueDescription() { return issueDescription; }
    public String getAddress() { return address; }
    public String getAddressDetail() { return addressDetail; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public BigDecimal getFinalCost() { return finalCost; }
    public String getTechnicianNote() { return technicianNote; }
    public String getCancelReason() { return cancelReason; }
    public String getRejectReason() { return rejectReason; }
    public List<DeviceCategoryDto> getSelectedDeviceCategories() { return selectedDeviceCategories; }
    public String getRequestTime() { return requestTime; }
    public String getAcceptedAt() { return acceptedAt; }
    public String getStartedAt() { return startedAt; }
    public String getCompletedAt() { return completedAt; }
    public String getCancelledAt() { return cancelledAt; }
    public String getRejectedAt() { return rejectedAt; }

    public void setServiceRequestId(String v) { this.serviceRequestId = v; }
    public void setServiceRequestCode(String v) { this.serviceRequestCode = v; }
    public void setCustomerId(String v) { this.customerId = v; }
    public void setCustomerName(String v) { this.customerName = v; }
    public void setCustomerPhoneNumber(String v) { this.customerPhoneNumber = v; }
    public void setCustomerProfilePhoto(String v) { this.customerProfilePhoto = v; }
    public void setTechnicianProfileId(String v) { this.technicianProfileId = v; }
    public void setStatus(String v) { this.status = v; }
    public void setIssueDescription(String v) { this.issueDescription = v; }
    public void setAddress(String v) { this.address = v; }
    public void setAddressDetail(String v) { this.addressDetail = v; }
    public void setEstimatedCost(BigDecimal v) { this.estimatedCost = v; }
    public void setFinalCost(BigDecimal v) { this.finalCost = v; }
    public void setTechnicianNote(String v) { this.technicianNote = v; }
    public void setCancelReason(String v) { this.cancelReason = v; }
    public void setRejectReason(String v) { this.rejectReason = v; }
    public void setSelectedDeviceCategories(List<DeviceCategoryDto> v) { this.selectedDeviceCategories = v; }
    public void setRequestTime(String v) { this.requestTime = v; }
    public void setAcceptedAt(String v) { this.acceptedAt = v; }
    public void setStartedAt(String v) { this.startedAt = v; }
    public void setCompletedAt(String v) { this.completedAt = v; }
    public void setCancelledAt(String v) { this.cancelledAt = v; }
    public void setRejectedAt(String v) { this.rejectedAt = v; }

    /** Friendly status label in Indonesian */
    public String getStatusLabel() {
        if (status == null) return "Unknown";
        switch (status.toUpperCase()) {
            case "PENDING":    return "Menunggu";
            case "ACCEPTED":   return "Diterima";
            case "IN_PROGRESS":return "Sedang Dikerjakan";
            case "COMPLETED":  return "Selesai";
            case "CANCELLED":  return "Dibatalkan";
            case "REJECTED":   return "Ditolak";
            default:           return status;
        }
    }

    /** Status badge color */
    public String getStatusColor() {
        if (status == null) return "#6B7680";
        switch (status.toUpperCase()) {
            case "PENDING":    return "#F39C12";
            case "ACCEPTED":   return "#2980B9";
            case "IN_PROGRESS":return "#8E44AD";
            case "COMPLETED":  return "#27AE60";
            case "CANCELLED":  return "#95A5A6";
            case "REJECTED":   return "#E74C3C";
            default:           return "#6B7680";
        }
    }
}
