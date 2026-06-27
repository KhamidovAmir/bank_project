package ru.khan.bank.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        description = "Данные личного профиля пользователя"
)
public record UserProfileResponse
        (
                @Schema(description = "Электронная почта пользователя", example = "example@gmail.com")
                String email,

                @Schema(description = "публичный ID пользователя формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                UUID publicId,

                @Schema(description = "Имя пользователя", example = "Ivan")
                String firstName,

                @Schema(description = "Фамилия пользователя", example = "Ivanov")
                String lastName,

                @Schema(description = "Роль пользователя", example = "CUSTOMER")
                String role,

                @Schema(description = "Статус пользователя", example = "ACTIVE")
                String status
        )
{}
