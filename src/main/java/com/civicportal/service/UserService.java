package com.civicportal.service;

import com.civicportal.dto.user.UserCreateRequest;
import com.civicportal.dto.user.UserResponse;
import com.civicportal.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();
}
