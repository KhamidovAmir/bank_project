package ru.khan.bank.operation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest
        (
                @NotNull
                UUID accountPublicId,
                @NotNull
                @Positive
                @DecimalMin(value = "10.00")
                BigDecimal amount,
                String description
        )
{
}
