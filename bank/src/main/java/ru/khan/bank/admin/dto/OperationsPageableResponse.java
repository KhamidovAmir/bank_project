package ru.khan.bank.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.operation.entity.OperationStatus;
import ru.khan.bank.operation.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        description = ""
)
public record OperationsPageableResponse
        (
                @Schema(
                        description = "Публичный ID операции формата UUID"
                )
                UUID operationPublicId,

                @Schema(
                        description = "Тип операции"
                )
                OperationType type,

                @Schema(
                        description = "Статус операции"
                )
                OperationStatus status,

                @Schema(
                        description = "ID отправителя"
                )
                Long from,

                @Schema(
                        description = "ID получателя"
                )
                Long to,

                @Schema(
                        description = "Сумма операция"
                )
                BigDecimal amount,

                @Schema(
                        description = "Валюта операции"
                )
                Currency currency,

                @Schema(
                        description = "Дата и время создания операции"
                )
                LocalDateTime createdAt,

                @Schema(
                        description = "Дата и время выполнения операции"
                )
                LocalDateTime completedAt
        )
{}
