package com.knowMoreQR.server.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails extends User implements UserDetails { // Extend User for convenience

    private final Long userId;
    private final String userType;

    public CustomUserDetails(
            Long userId,
            String userType,
            String username, // email
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
        this.userId = userId;
        this.userType = userType;
    }

    // Add getters for custom fields
    public Long getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    // You can override other UserDetails methods if needed, but extending User handles most.
} 