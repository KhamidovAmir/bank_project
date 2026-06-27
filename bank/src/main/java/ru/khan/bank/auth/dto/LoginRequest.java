package ru.khan.bank.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        description = "Тело запроса на авторизацию"
)
public record LoginRequest
        (
                @Schema(description = "email почта",
                        example = "example@gmail.com")
                @NotBlank
                @Email
                String email,

                @Schema(description = "Пароль пользователя",
                        example = "qwerty123!")
                @NotBlank
                String password
        )
{}
