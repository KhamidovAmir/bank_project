package ru.khan.bank.auth.dto;

import java.util.UUID;

public record JwtUser
        (
                UUID publicId,
                String email,
                String role
        )
{}
