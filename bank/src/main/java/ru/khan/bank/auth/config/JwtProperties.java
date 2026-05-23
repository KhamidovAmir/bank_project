package ru.khan.bank.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties
        (
                String secret,
                Long expiration
        )
{}
