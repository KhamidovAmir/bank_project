package ru.khan.bank.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.account.entity.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        description = "Полная информация по созданному счету"
)
public record CreateAccountResponse
        (
                @Schema(description = "Публичный ID формата UUID")
                UUID publicId,

                @Schema(description = "Номер счета")
                String accountNumber,

                @Schema(description = "Баланс счета")
                BigDecimal balance,

                @Schema(description = "Статус счета")
                AccountStatus status
        )
{}
