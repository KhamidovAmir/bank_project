package ru.khan.bank.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.account.entity.AccountStatus;

import java.util.UUID;

@Schema(description = "Краткая информация по счету для пагинации")
public record AccountsPageableResponse
        (
                @Schema(
                        description = "Публичный ID счета формата UUID",
                        example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92"
                )
                UUID accountPublicId,

                @Schema(
                        description = "Номер счета",
                        example = "OP-2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92"
                )
                String accountNumber,

                @Schema(
                        description = "Публичный ID владельца счета формата UUID",
                        example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92"
                )
                UUID ownerPublicId,

                @Schema(
                        description = "Имя владельца счета",
                        example = "Ivan"
                )
                String ownerFirstName,

                @Schema(
                        description = "Фамилия владельца счета",
                        example = "Ivanov"
                )
                String ownerLastName,

                @Schema(
                        description = "Статус счета",
                        example = "ACTIVE"
                )
                AccountStatus status
        ) {
}
