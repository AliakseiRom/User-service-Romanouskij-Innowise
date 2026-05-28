package com.innowise.userservice.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtUser {

    private Long userId;

    private String login;

    private String role;
}
