package com.civicportal.dto.ward;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WardRequest {
    @NotBlank(message = "Ward code is required")
    @Size(max = 20, message = "Ward code must be 20 characters or fewer")
    private String code;

    @NotBlank(message = "Ward name is required")
    @Size(max = 100, message = "Ward name must be 100 characters or fewer")
    private String name;

    @Size(max = 500, message = "Description must be 500 characters or fewer")
    private String description;

    public WardRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}