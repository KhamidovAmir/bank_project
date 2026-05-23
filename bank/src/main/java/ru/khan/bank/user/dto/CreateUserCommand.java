package ru.khan.bank.user.dto;

public record CreateUserCommand
        (
            String email,
            String passwordHash,
            String firstName,
            String lastName
        )
{}
