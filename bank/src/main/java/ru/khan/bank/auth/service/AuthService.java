package ru.khan.bank.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.auth.dto.LoginRequest;
import ru.khan.bank.auth.dto.TokenResponse;
import ru.khan.bank.common.exception.exceptions.BadRequestException;
import ru.khan.bank.common.exception.exceptions.ConflictException;
import ru.khan.bank.user.dto.CreateUserCommand;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    public AuthService(UserService userService, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public TokenResponse register(CreateUserRequest request) {

        String passHash = passwordEncoder.encode(request.password());

        User user = userService.createUser(new CreateUserCommand
                (
                        request.email(),
                        passHash,
                        request.firstName(),
                        request.lastName()
                )
        );

        return new TokenResponse(tokenService.generateToken(user));

    }

    public TokenResponse login(LoginRequest request){

        User user = userService.getUserByEmailOrThrow(request.email());

        if(!passwordMatch(request.password(), user))
            throw new BadRequestException("Invalid email or password");

        if (!userService.isActive(user))
            throw new ConflictException("Authentication failed");

        return new TokenResponse(tokenService.generateToken(user));
    }
    private boolean passwordMatch(String password, User user) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}
