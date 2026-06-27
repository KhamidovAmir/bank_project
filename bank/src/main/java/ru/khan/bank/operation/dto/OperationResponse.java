package ru.khan.bank.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.khan.bank.operation.entity.OperationStatus;

public record OperationResponse
        (
                @Schema(
                        description = "Статус операции",
                        example = "PENDING"
                )
                OperationStatus status
        )
{}
