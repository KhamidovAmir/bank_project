package ru.khan.bank.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.khan.bank.account.entity.Currency;

public record CreateAccountRequest
        (
                @Schema(name = "Валюта счета")
                @NotNull
                Currency currency
        )
{}
