package com.innowise.userservice.integration.config;

import com.innowise.userservice.security.JwtService;
import com.innowise.userservice.security.JwtUser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtService jwtService() {
        return mock(JwtService.class);
    }

    public static void mockSecurityContext(Long userId, String role) {
        Authentication auth = mock(Authentication.class);

        var jwtUser = new JwtUser(
                userId,
                "test",
                role
        );

        SecurityContext context = mock(SecurityContext.class);

        SecurityContextHolder.setContext(context);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}