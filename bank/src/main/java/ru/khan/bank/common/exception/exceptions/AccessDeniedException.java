package ru.khan.bank.common.exception.exceptions;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
