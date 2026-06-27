package ru.khan.bank.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.auth.dto.LoginRequest;
import ru.khan.bank.auth.dto.TokenResponse;

@RequestMapping("/auth")
@Tag(name = "Контроллер работы с авторизацией", description = "Контроллер контура работы с авторизацией пользователей и выдачи JWT токена")
public interface AuthApi {

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя и выдача токена")
    ResponseEntity<TokenResponse> register(@Valid @RequestBody CreateUserRequest request);

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя и выдача токена")
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);
    
}
