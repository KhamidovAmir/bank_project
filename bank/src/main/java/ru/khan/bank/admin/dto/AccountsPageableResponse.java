package ru.khan.bank.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.account.entity.AccountStatus;

import java.util.UUID;

@Schema(description = "Краткая информация по счету для пагинации")
public record AccountsPageableResponse
        (
                @Schema(
                        description = "Публичный ID счета формата UUID"
                )
                UUID accountPublicId,

                @Schema(
                        description = "Номер счета"
                )
                String accountNumber,

                @Schema(
                        description = "Публичный ID владельца счета формата UUID"
                )
                UUID ownerPublicId,

                @Schema(
                        description = "Имя владельца счета"
                )
                String ownerFirstName,

                @Schema(
                        description = "Фамилия владельца счета"
                )
                String ownerLastName,

                @Schema(
                        description = "Статус счета"
                )
                AccountStatus status
        ) {
}
