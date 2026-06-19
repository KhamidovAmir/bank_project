package ru.khan.bank.operation.dto;

import ru.khan.bank.operation.entity.OperationStatus;

public record OperationResponse
        (
                OperationStatus status
        )
{}
