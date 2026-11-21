package com.civicportal.controller;

import com.civicportal.dto.user.UserResponse;
import com.civicportal.dto.user.UserUpdateRequest;
import com.civicportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") Long userId, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}