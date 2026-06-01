package ru.khan.bank.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.mapper.AccountMapper;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.account.service.AccountNumberGenerator;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.repository.UserRepository;
import ru.khan.bank.user.service.UserService;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class AccountServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @AfterEach
    void cleanDb(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createAccount_shouldReturnAccountForCurrentUser(){

        User user = createUser();

        when(userService.getCurrentUser()).thenReturn(user);

        CreateAccountRequest createAccountRequest = new CreateAccountRequest(Currency.RUB);

        var result = accountService.createAccount(createAccountRequest);

        assertThat(result).isNotNull();

        assertThat(result.publicId()).isNotNull();

        Account account = accountRepository.findByPublicId(result.publicId()).orElseThrow();

        assertThat(account.getOwner().getId()).isEqualTo(user.getId());
    }

    private User createUser(){
        return userRepository.save(new User(
                UUID.randomUUID() + "@gmail.com",
                "password",
                "test",
                "test",
                UserRole.CUSTOMER
        ));
    }
}
