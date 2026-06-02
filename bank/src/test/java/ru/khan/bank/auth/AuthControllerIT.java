package ru.khan.bank.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.auth.dto.LoginRequest;
import ru.khan.bank.auth.service.AuthService;
import ru.khan.bank.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class AuthControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanDb(){
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturnToken() throws Exception {
        var request = new CreateUserRequest(
                UUID.randomUUID() + "@gmail.com",
                "passwordrdrd",
                "test",
                "test"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    void register_shouldThrowExceptionDuplicateEmail() throws Exception {
        String email = UUID.randomUUID() + "@gmail.com";
        String password = "passwordrdrd";

        authService.register(new CreateUserRequest(email, password, "test", "test"));

        var request = new CreateUserRequest(
                email,
                password,
                "test",
                "test"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

    }
    @Test
    void login_shouldReturnToken() throws Exception {
        String email = UUID.randomUUID() + "@gmail.com";
        String password = "passwordrdrd";

        authService.register(new CreateUserRequest(email, password, "test", "test"));

        var loginRequest = new LoginRequest(email, password);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    void login_shouldThrowExceptionWrongPassword() throws Exception {
        String email = UUID.randomUUID() + "@gmail.com";
        String password = "passwordrdrd";

        authService.register(new CreateUserRequest(email, password, "test", "test"));

        var loginRequest = new LoginRequest(email, "asdasdadadasda");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());
    }
}
