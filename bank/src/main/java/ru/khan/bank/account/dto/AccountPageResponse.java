package ru.khan.bank.account.dto;

import ru.khan.bank.account.entity.AccountStatus;
import ru.khan.bank.account.entity.Currency;

import java.math.BigDecimal;

public record AccountPageResponse
        (
                String accountNumber,
                BigDecimal balance,
                Currency currency,
                AccountStatus status
        )
{}
