package ru.khan.bank.operation.dto;

import jakarta.validation.constraints.Min;
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
                @Min(10)
                BigDecimal amount,
                String description
        ) {}
