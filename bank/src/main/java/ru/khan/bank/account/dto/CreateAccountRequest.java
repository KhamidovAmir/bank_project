package ru.khan.bank.account.dto;

import jakarta.validation.constraints.NotNull;
import ru.khan.bank.account.entity.Currency;

public record CreateAccountRequest
        (
                @NotNull
                Currency currency
        )
{}
