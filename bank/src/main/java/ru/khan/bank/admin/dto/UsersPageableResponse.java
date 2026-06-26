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
                        description = "Публичный ID пользователя формата UUID"
                )
                UUID publicId,

                @Schema(
                        description = "Имя пользователя"
                )
                String firstName,

                @Schema(
                        description = "Фамилия пользователя"
                )
                String lastName,

                @Schema(
                        description = "Роль пользователя"
                )
                UserRole role,

                @Schema(
                        description = "Статус пользователя"
                )
                UserStatus status,

                @Schema(
                        description = "Дата и время создания аккаунта для пользователя"
                )
                LocalDateTime createdAt
        )
{}
