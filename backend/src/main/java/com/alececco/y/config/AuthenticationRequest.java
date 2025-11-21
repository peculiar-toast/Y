package com.alececco.y.config;

public record AuthenticationRequest(
        String username,
        String password
) {}
