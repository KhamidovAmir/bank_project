package ru.khan.bank.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.auth.dto.TokenResponse;
import ru.khan.bank.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody CreateUserRequest request){
        return ResponseEntity.status(201).body(authService.register(request));
    }
}
