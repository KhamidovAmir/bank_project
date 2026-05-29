package ru.khan.bank.admin.dto;

import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersPageableResponse
        (
                UUID publicId,
                String firstName,
                String lastName,
                UserRole role,
                UserStatus status,
                LocalDateTime createdAt
        )
{}
