package com.civicportal.dto.user;

import com.civicportal.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(max = 100, message = "Full name must be 100 characters or fewer")
    private String fullName;

    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email must be 150 characters or fewer")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number must contain exactly 10 digits"
    )
    private String mobile;

    @Positive(message = "Role ID must be a positive number")
    private Long roleId;

    private UserStatus status;

    public UserUpdateRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
