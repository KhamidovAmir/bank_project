package ru.khan.bank.user.dto;

import java.util.UUID;

public record UserProfileResponse
        (
                String email,
                UUID publicId,
                String firstName,
                String lastName,
                String role,
                String status
        )
{}
