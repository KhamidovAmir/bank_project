package ru.khan.bank.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT токена, выдается при успешной регистрации/авторизации")
public record TokenResponse
        (
                @Schema(description = "JWT токен, стандартного вида")
                String token
        )
{}
