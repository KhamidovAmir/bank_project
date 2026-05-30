package ru.khan.bank.operation.dto;

import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.operation.entity.OperationStatus;
import ru.khan.bank.operation.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MoneyOperationsResponse
        (
                UUID publicId,
                String operationNumber,
                OperationType type,
                OperationStatus status,
                Long fromAccountId,
                Long toAccountId,
                BigDecimal amount,
                Currency currency,
                LocalDateTime createdAt,
                LocalDateTime completedAt
        ) {
}
