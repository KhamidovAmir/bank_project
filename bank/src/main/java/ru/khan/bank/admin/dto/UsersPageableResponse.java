package ru.khan.bank.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        description = "Краткая информация про пользователя для пагинации"
)
public record UsersPageableResponse
        (
                @Schema(
                        description = "Публичный ID пользователя формата UUID",
                        example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92"
                )
                UUID publicId,

                @Schema(
                        description = "Имя пользователя",
                        example = "Ivan"
                )
                String firstName,

                @Schema(
                        description = "Фамилия пользователя",
                        example = "Ivanov"
                )
                String lastName,

                @Schema(
                        description = "Роль пользователя",
                        example = "CUSTOMER"
                )
                UserRole role,

                @Schema(
                        description = "Статус пользователя",
                        example = "ACTIVE"
                )
                UserStatus status,

                @Schema(
                        description = "Дата и время создания аккаунта для пользователя",
                        example = "2024-05-20T14:35:10.123"
                )
                LocalDateTime createdAt
        )
{}
