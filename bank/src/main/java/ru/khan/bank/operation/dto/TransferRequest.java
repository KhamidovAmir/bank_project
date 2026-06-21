package ru.khan.bank.operation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest
        (
                @NotNull
                UUID accountPublicIdFrom,
                @NotNull
                UUID accountPublicIdTo,
                @NotNull
                @Positive
                @DecimalMin(value = "10.00")
                BigDecimal amount,
                String description
        ) {}
