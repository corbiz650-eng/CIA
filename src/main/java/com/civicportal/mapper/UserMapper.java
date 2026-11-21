package com.civicportal.mapper;

import com.civicportal.dto.user.UserResponse;
import com.civicportal.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        String roleName = user.getRole() == null
                ? null
                : user.getRole().getName();

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus(),
                roleName
        );
    }
}