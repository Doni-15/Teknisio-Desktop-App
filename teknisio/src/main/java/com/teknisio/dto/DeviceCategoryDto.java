package com.teknisio.dto;

/**
 * Matches backend DeviceCategoryResponse record.
 */
public class DeviceCategoryDto {
    private String deviceCategoryId;
    private String name;
    private String icon;
    private Boolean active;

    public DeviceCategoryDto() {}

    public String getDeviceCategoryId() { return deviceCategoryId; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public Boolean getActive() { return active; }

    public void setDeviceCategoryId(String deviceCategoryId) { this.deviceCategoryId = deviceCategoryId; }
    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setActive(Boolean active) { this.active = active; }
}
