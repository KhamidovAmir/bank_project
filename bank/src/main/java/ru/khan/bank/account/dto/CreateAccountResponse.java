package ru.khan.bank.account.dto;

import ru.khan.bank.account.entity.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountResponse
        (
                UUID publicId,
                String account,
                BigDecimal balance,
                AccountStatus status
        )
{}
