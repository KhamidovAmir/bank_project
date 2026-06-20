package ru.khan.bank.common.exception.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus status,
        Integer error,
        String message,
        String timestamp
) {}
