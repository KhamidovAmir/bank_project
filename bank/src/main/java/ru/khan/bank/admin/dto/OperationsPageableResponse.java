package ru.khan.bank.admin.dto;

import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.operation.entity.OperationStatus;
import ru.khan.bank.operation.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationsPageableResponse
        (
                UUID operationPublicId,
                OperationType type,
                OperationStatus status,
                Long from,
                Long to,
                BigDecimal amount,
                Currency currency,
                LocalDateTime createdAt,
                LocalDateTime completedAt
        )
{}
