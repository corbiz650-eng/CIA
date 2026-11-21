package com.civicportal.dto.ward;

public class WardResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private boolean active;

    public WardResponse() {
    }

    public WardResponse(Long id, String code, String name, String description, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
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