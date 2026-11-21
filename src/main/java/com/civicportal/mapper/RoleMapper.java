package com.civicportal.mapper;

import com.civicportal.dto.role.RoleResponse;
import com.civicportal.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive()
        );
    }
}