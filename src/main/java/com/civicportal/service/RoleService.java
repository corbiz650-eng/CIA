package com.civicportal.service;

import com.civicportal.dto.role.RoleRequest;
import com.civicportal.dto.role.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long roleId, RoleRequest request);

    RoleResponse getRoleById(Long roleId);

    List<RoleResponse> getAllRoles();

    RoleResponse setRoleActive(Long roleId, boolean active);
}
