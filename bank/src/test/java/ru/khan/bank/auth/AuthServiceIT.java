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
import ru.khan.bank.auth.dto.LoginRequest;
import ru.khan.bank.auth.dto.TokenResponse;
import ru.khan.bank.auth.service.AuthService;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class AuthServiceIT {

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

        var request = createUserRequest();
        var response = authService.register(request);

        assertThat(response).isNotNull()
                .isInstanceOf(TokenResponse.class);
    }
    @Test
    void register_shouldThrowExceptionDuplicateEmail(){
        String email = "test@gmail.com";

        var firstRequest = createUserRequest(email);
        var secondRequest = createUserRequest(email);

        authService.register(firstRequest);
        assertThatThrownBy(() -> authService.register(secondRequest))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    void login_shouldReturnTokenResponse(){
        var requestCreateUser = createUserRequest();
        authService.register(requestCreateUser);

        var requestLogin = new LoginRequest(requestCreateUser.email(), requestCreateUser.password());

        var response = authService.login(requestLogin);

        assertThat(response).isNotNull()
                .isInstanceOf(TokenResponse.class);
    }
    @Test
    void login_shouldThrowExceptionWrongPassword(){
        var requestCreateUser = createUserRequest();
        authService.register(requestCreateUser);

        var requestLogin = new LoginRequest(requestCreateUser.email(), "anotherPassword");
        assertThatThrownBy(() -> authService.login(requestLogin))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    void login_shouldThrowExceptionUserIsNotActive(){
        var requestCreateUser = createUserRequest();
        authService.register(requestCreateUser);

        User user = userRepository.findByEmail(requestCreateUser.email()).orElseThrow();
        user.block();
        userRepository.save(user);

        var requestLogin = new LoginRequest(requestCreateUser.email(), requestCreateUser.password());

        assertThatThrownBy(() -> authService.login(requestLogin))
                .isInstanceOf(RuntimeException.class);
    }

    private CreateUserRequest createUserRequest(String email){
        return new CreateUserRequest(email, "passwordrdrd", "test", "test");
    }
    private CreateUserRequest createUserRequest(){
        return new CreateUserRequest(UUID.randomUUID().toString(), "passwordrdrd", "test", "test");
    }


}
