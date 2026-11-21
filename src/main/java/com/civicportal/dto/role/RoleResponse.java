package com.civicportal.dto.role;

public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private boolean active;

    public RoleResponse() {
    }

    public RoleResponse(Long id, String name, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}