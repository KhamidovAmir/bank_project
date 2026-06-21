package ru.khan.bank.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.khan.bank.common.exception.dto.ErrorResponse;
import ru.khan.bank.common.exception.exceptions.*;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, 403, e.getMessage());
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, 401, e.getMessage());
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.CONFLICT, 409, e.getMessage());
    }
    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ErrorResponse> handleIdempotencyConflictException(IdempotencyConflictException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, 422, e.getMessage());
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, 404, e.getMessage());
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, 400, e.getMessage());
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, 400, e.getMessage());
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, 403, e.getMessage());
    }
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e) {
        log.warn(e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, 401, e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        log.error("Unexpected error", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, 500,"Internal Server Error");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Integer code, String message) {
        ErrorResponse body = new ErrorResponse(status, code, message, Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
