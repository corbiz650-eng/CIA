package com.civicportal.security;

import com.civicportal.entity.User;
import com.civicportal.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final User user;

    private SecurityUser(User user) {
        this.user = user;
    }

    public static SecurityUser from(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        return new SecurityUser(user);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null || !user.getRole().isActive()) {
            return List.of();
        }

        String roleName = user.getRole().getName();
        String authority = roleName.startsWith("ROLE_")
                ? roleName
                : "ROLE_" + roleName;

        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE
                && user.getRole() != null
                && user.getRole().isActive();
    }
}
