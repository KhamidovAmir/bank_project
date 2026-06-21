package ru.khan.bank.operation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest
        (
                @NotBlank
                UUID accountPublicId,
                @NotNull
                @Positive
                @Min(10)
                BigDecimal amount,
                String description
        )
{}
