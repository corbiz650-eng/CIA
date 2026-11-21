package com.civicportal.dto.auth;

public class LoginResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String email;
    private String fullName;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            Long userId,
            String email,
            String fullName,
            String role
    ) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
