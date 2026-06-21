package ru.khan.bank.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class AccountControllerIT {

    private static final String BEARER_PREFIX = "Bearer ";

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void cleanDb(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void createAccount_shouldReturnIsCreated() throws Exception {
        String token = registerAndGetToken();

        var request = new CreateAccountRequest(Currency.RUB);

        mockMvc.perform(post("/accounts")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getMyAccounts_shouldReturnIsOk() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(post("/accounts/my")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "CREATED_AT")
                .param("asc", "true"))
                .andExpect(status().isOk());

    }
    @Test
    void getMyAccounts_shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post("/accounts/my")
                        .param("sort", "CREATED_AT"))
                .andExpect(status().isForbidden());
    }
    @Test
    void getMyAccounts_shouldReturnBadRequestWhenSortIsInvalid() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(post("/accounts/my")
                        .header("Authorization", "Bearer " + token)
                        .param("sort", "WRONG_SORT"))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void getAccount_shouldReturnIsNotFound() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(get("/accounts/{publicId}", UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccount_shouldReturnIsOk() throws Exception {
        String token = registerAndGetToken();

        String accountId = createAccount(token);

        mockMvc.perform(get("/accounts/{publicId}", accountId)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk());
    }

    private String registerAndGetToken() throws Exception {
        var request = new CreateUserRequest(
                UUID.randomUUID() + "@gmail.com",
                "passwordrdrd",
                "test",
                "test"
        );

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asString();
    }

    private String createAccount(String token) throws Exception {
        var request = new CreateAccountRequest(Currency.RUB);

        MvcResult result = mockMvc.perform(post("/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("publicId").asString();
    }
}
