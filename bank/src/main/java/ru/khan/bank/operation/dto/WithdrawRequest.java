package ru.khan.bank.operation.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest
        (
                @NotNull
                UUID accountPublicId,
                @NotNull
                @Positive
                @Digits(integer=19, fraction=2)
                BigDecimal amount,
                String description
        )
{}
