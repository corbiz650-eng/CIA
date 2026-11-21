package com.civicportal.service;

import com.civicportal.dto.role.RoleRequest;
import com.civicportal.dto.role.RoleResponse;
import com.civicportal.entity.Role;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.DuplicateResourceException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.RoleMapper;
import com.civicportal.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        String name = normalizeName(request.getName());

        if (roleRepository.existsByName(name)) {
            throw new DuplicateResourceException(
                    "A role with this name already exists");
        }

        Role role = new Role(name, normalizeDescription(request.getDescription()));
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = findRole(roleId);
        String name = normalizeName(request.getName());

        if (!name.equalsIgnoreCase(role.getName())
                && roleRepository.existsByName(name)) {
            throw new DuplicateResourceException(
                    "A role with this name already exists");
        }

        role.setName(name);
        role.setDescription(normalizeDescription(request.getDescription()));
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long roleId) {
        return roleMapper.toResponse(findRole(roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoleResponse setRoleActive(Long roleId, boolean active) {
        Role role = findRole(roleId);
        role.setActive(active);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    private Role findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found with ID: " + roleId));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Role name must not be blank");
        }
        return name.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank()
                ? null
                : description.trim();
    }
}
