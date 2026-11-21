package com.civicportal.service;

import com.civicportal.dto.auth.LoginRequest;
import com.civicportal.dto.auth.LoginResponse;
import com.civicportal.dto.auth.RegisterRequest;
import com.civicportal.dto.auth.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    LoginResponse refresh(String authorizationHeader);
}