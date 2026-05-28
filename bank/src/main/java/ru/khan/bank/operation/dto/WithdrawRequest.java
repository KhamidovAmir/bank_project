package ru.khan.bank.operation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest
        (
                UUID accountPublicId,
                BigDecimal amount,
                String description
        )
{}
