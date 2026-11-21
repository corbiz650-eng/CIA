package com.civicportal.dto.user;

import com.civicportal.enums.UserStatus;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserStatus status;
    private String roleName;

    public UserResponse() {
    }

    public UserResponse(Long id, String fullName, String email, UserStatus status, String roleName) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getRoleName() {
        return roleName;
    }
}