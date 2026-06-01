package ru.khan.bank.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.auth.dto.TokenResponse;
import ru.khan.bank.auth.service.AuthService;
import ru.khan.bank.user.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class AuthServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanDb(){
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturnTokenResponse(){

        var request = new CreateUserRequest(
                UUID.randomUUID().toString(),
                "passwordrdrd",
                "test",
                "test"
                );
        var response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(TokenResponse.class);
    }
    @Test
    void register_shouldThrowExceptionDuplicateEmail(){
        String email = "test@gmail.com";

        var firstRequest = new CreateUserRequest(email, "passwordrdrd", "test", "test");
        var secondRequest = new CreateUserRequest(email, "passwordrdrd", "test", "test");

        authService.register(firstRequest);
        assertThatThrownBy(() -> authService.register(secondRequest))
                .isInstanceOf(RuntimeException.class);
    }


}
