package ru.khan.bank.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.auth.dto.LoginRequest;
import ru.khan.bank.auth.dto.TokenResponse;
import ru.khan.bank.auth.service.AuthService;

@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<TokenResponse> register(CreateUserRequest request){
        return ResponseEntity.status(201).body(authService.register(request));
    }

    public ResponseEntity<TokenResponse> login(LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
