package com.innowise.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static JwtUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return (JwtUser) auth.getPrincipal();
    }
}
