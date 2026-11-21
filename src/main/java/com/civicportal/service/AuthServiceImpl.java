package com.civicportal.service;

import com.civicportal.dto.auth.LoginRequest;
import com.civicportal.dto.auth.LoginResponse;
import com.civicportal.dto.auth.RegisterRequest;
import com.civicportal.dto.auth.UserResponse;
import com.civicportal.entity.Role;
import com.civicportal.entity.User;
import com.civicportal.enums.UserStatus;
import com.civicportal.exception.DuplicateResourceException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.exception.UnauthorizedException;
import com.civicportal.repository.RoleRepository;
import com.civicportal.repository.UserRepository;
import com.civicportal.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String CITIZEN_ROLE_NAME = "CITIZEN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("This account is not active");
        }

        if (user.getRole() == null || !user.getRole().isActive()) {
            throw new UnauthorizedException("This account has no active role");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName()
        );
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String mobile = request.getMobile().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "An account with this email already exists");
        }

        if (userRepository.existsByMobile(mobile)) {
            throw new DuplicateResourceException(
                    "An account with this mobile number already exists");
        }

        Role citizenRole = roleRepository.findByName(CITIZEN_ROLE_NAME)
                .filter(Role::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The active citizen role is not configured"));

        User user = new User(
                request.getFullName().trim(),
                email,
                passwordEncoder.encode(request.getPassword())
        );
        user.setMobile(mobile);
        user.setRole(citizenRole);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                citizenRole.getName()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refresh(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("A Bearer token is required");
        }

        String currentToken =
                authorizationHeader.substring("Bearer ".length()).trim();

        final String email;
        try {
            email = jwtService.extractUsername(currentToken);
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("The token is invalid or expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("The token user no longer exists"));

        if (user.getStatus() != UserStatus.ACTIVE
                || user.getRole() == null
                || !user.getRole().isActive()) {
            throw new UnauthorizedException("This account is not active");
        }

        String newToken = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                newToken,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName()
        );
    }
}
