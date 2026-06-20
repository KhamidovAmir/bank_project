package ru.khan.bank.common.exception.exceptions;

public class IdempotencyConflictException extends BusinessException {
    public IdempotencyConflictException(String message) {
        super(message);
    }
}
