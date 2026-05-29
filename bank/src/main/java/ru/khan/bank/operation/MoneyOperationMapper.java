package ru.khan.bank.operation;

import org.springframework.stereotype.Component;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.operation.entity.OperationStatus;
import ru.khan.bank.operation.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MoneyOperationMapper {

    public OperationsPageableResponse toOperationsPageable(UUID operationPublicId, OperationType type, OperationStatus status, Long from, Long to, BigDecimal amount, Currency currency, LocalDateTime createdAt, LocalDateTime completedAt) {
        return new OperationsPageableResponse(
                operationPublicId,
                type,
                status,
                from,
                to,
                amount,
                currency,
                createdAt,
                completedAt
                );
    }
}
