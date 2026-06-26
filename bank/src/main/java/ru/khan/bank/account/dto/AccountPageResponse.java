package ru.khan.bank.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.account.entity.AccountStatus;
import ru.khan.bank.account.entity.Currency;

import java.math.BigDecimal;

@Schema(
        description = "Ответ сокращенного вида для пагинации"
)
public record AccountPageResponse
        (
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
        )
{}
