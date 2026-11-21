package com.civicportal.dto.complaint;

import jakarta.validation.constraints.*;

public class ComplaintCreateRequest {
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must be 5000 characters or fewer")
    private String description;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @NotNull(message = "Ward ID is required")
    @Positive(message = "Ward ID must be a positive number")
    private Long wardId;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must be 500 characters or fewer")
    private String address;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
    private Double longitude;

    public ComplaintCreateRequest() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
