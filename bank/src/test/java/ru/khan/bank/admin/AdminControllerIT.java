package ru.khan.bank.admin;

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
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.auth.dto.CreateUserRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminControllerIT {

    private static final String BEARER_PREFIX = "Bearer ";

    @ServiceConnection
    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void blockAccount_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(patch("/admin/accounts/{accountPublicId}/block",  UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isForbidden());
    }
    @Test
    void blockAccount_shouldReturnOk_whenUserIsAdmin() throws Exception {
        String token = registerAndGetToken();
        String accountId = createAccount(token);

        mockMvc.perform(patch("/admin/accounts/{accountPublicId}/block",  accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isForbidden());
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
