package ru.khan.bank.auth.dto;

import jakarta.validation.constraints.*;


public record CreateUserRequest
        (
                @NotBlank
                @Email
                String email,

                @NotBlank
                @Max(value = 30, message = "Your password is too long, max 30 length")
                @Min(value = 8, message = "Your password is too easy, min 8 length")
                String password,

                @NotBlank
                String firstName,

                @NotBlank
                String lastName
        )
{}
