package com.civicportal.service;

import com.civicportal.dto.user.UserCreateRequest;
import com.civicportal.dto.user.UserResponse;
import com.civicportal.dto.user.UserUpdateRequest;
import com.civicportal.entity.Role;
import com.civicportal.entity.User;
import com.civicportal.enums.UserStatus;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.DuplicateResourceException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.UserMapper;
import com.civicportal.repository.RoleRepository;
import com.civicportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = normalizeEmail(request.getEmail());
        String mobile = request.getMobile().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "An account with this email already exists");
        }
        if (userRepository.existsByMobile(mobile)) {
            throw new DuplicateResourceException(
                    "An account with this mobile number already exists");
        }

        Role role = findActiveRole(request.getRoleId());
        User user = new User(
                request.getFullName().trim(),
                email,
                passwordEncoder.encode(request.getPassword())
        );
        user.setMobile(mobile);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = findUser(userId);

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();
            if (fullName.isEmpty()) {
                throw new BadRequestException("Full name must not be blank");
            }
            user.setFullName(fullName);
        }

        if (request.getEmail() != null) {
            String email = normalizeEmail(request.getEmail());
            if (email.isEmpty()) {
                throw new BadRequestException("Email must not be blank");
            }
            if (!email.equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmailIgnoreCase(email)) {
                throw new DuplicateResourceException(
                        "An account with this email already exists");
            }
            user.setEmail(email);
        }

        if (request.getMobile() != null) {
            String mobile = request.getMobile().trim();
            if (!mobile.equals(user.getMobile())
                    && userRepository.existsByMobile(mobile)) {
                throw new DuplicateResourceException(
                        "An account with this mobile number already exists");
            }
            user.setMobile(mobile);
        }

        if (request.getRoleId() != null) {
            user.setRole(findActiveRole(request.getRoleId()));
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        return userMapper.toResponse(findUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + userId));
    }

    private Role findActiveRole(Long roleId) {
        return roleRepository.findById(roleId)
                .filter(Role::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active role not found with ID: " + roleId));
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email must not be null");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
