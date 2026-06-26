package ru.khan.bank.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.account.entity.AccountStatus;
import ru.khan.bank.account.entity.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        description = "Полная информация по счету"
)
public record AccountResponse
        (
                @Schema(
                        name = "Публичный ID",
                        description = "Публичный ID формата UUID"
                )
                UUID publicId,

                @Schema(
                        name = "Номер счета"
                )
                String accountNumber,

                @Schema(
                        name = "Баланс счета"
                )
                BigDecimal balance,

                @Schema(
                        name = "Валюта счета"
                )
                Currency currency,

                @Schema(
                        name = "Статус счета"
                )
                AccountStatus status
        ) {}
