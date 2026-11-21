package com.civicportal.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleRequest {
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must be 50 characters or fewer")
    private String name;

    @Size(max = 255, message = "Description must be 255 character or fewer")
    private String description;

    public RoleRequest() {
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