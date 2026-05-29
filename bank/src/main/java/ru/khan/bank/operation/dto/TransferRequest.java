package ru.khan.bank.operation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest
        (
                UUID accountPublicIdFrom,
                UUID accountPublicIdTo,
                BigDecimal amount,
                String description
        ) {}
