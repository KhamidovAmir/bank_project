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
                        description = "Публичный ID операции формата UUID",
                        example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92"
                )
                UUID operationPublicId,

                @Schema(
                        description = "Тип операции",
                        example = "WITHDRAW"
                )
                OperationType type,

                @Schema(
                        description = "Статус операции",
                        example = "PENDING"
                )
                OperationStatus status,

                @Schema(
                        description = "ID отправителя",
                        example = "1"
                )
                Long from,

                @Schema(
                        description = "ID получателя",
                        example = "2"
                )
                Long to,

                @Schema(
                        description = "Сумма операция",
                        example = "1.00"
                )
                BigDecimal amount,

                @Schema(
                        description = "Валюта операции",
                        example = "RUB"
                )
                Currency currency,

                @Schema(
                        description = "Дата и время создания операции",
                        example = "2024-05-20T14:35:10.123"
                )
                LocalDateTime createdAt,

                @Schema(
                        description = "Дата и время выполнения операции",
                        example = "2024-05-20T14:35:10.123"
                )
                LocalDateTime completedAt
        )
{}
