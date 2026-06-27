package ru.khan.bank.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Тело запрос на регистрацию пользователя")
public record CreateUserRequest
        (
                @Schema(description = "Электронная почта пользователя", example = "example@gmail.com")
                @NotBlank
                @Email
                String email,

                @Schema(description = "Пароль пользователя", example = "qwerty123!")
                @NotBlank
                @Size(min = 8, message = "Your password is too easy, min 8 length")
                String password,

                @Schema(description = "Имя пользователя", example = "Ivan")
                @NotBlank
                String firstName,

                @Schema(description = "Фамилия пользователя", example = "Ivanov")
                @NotBlank
                String lastName
        )
{}
