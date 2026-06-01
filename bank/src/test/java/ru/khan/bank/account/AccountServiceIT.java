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
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.account.entity.AccountStatus;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.repository.UserRepository;
import ru.khan.bank.user.service.UserService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void getMyAccounts_shouldReturnAccountsOnlyForCurrentUser(){
        var anotherUser = createUser();
        var user = createUser();

        var accountForAnotherUser = createAccount(anotherUser, Currency.RUB);
        var accountForUser = createAccount(user, Currency.RUB);

        when(userService.getCurrentUser()).thenReturn(user);

        var result = accountService.getMyAccounts(10, 0, AccountSort.CREATED_AT, true);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);

        assertThat(result.getContent())
                .allMatch(
                        a -> a.accountNumber().equals(accountForUser.getAccountNumber())
                );

    }

    @Test
    void getAccountByPublicId_shouldReturnAccount(){
        var user = createUser();
        var account = createAccount(user, Currency.RUB);

        when(userService.getCurrentUser()).thenReturn(user);

        var result = accountService.getAccount(account.getPublicId());

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(account.getPublicId());
    }

    @Test
    void getAccountByPublicId_shouldThrowExceptionIsNotYourAccount(){
        var anotherUser = createUser();
        var accountForAnother = createAccount(anotherUser, Currency.RUB);

        var user = createUser();

        when(userService.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> accountService.getAccountByPublicId(accountForAnother.getPublicId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void closeAccount_shouldCloseAccount(){
        var user = createUser();
        var account = createAccount(user, Currency.RUB);

        when(userService.getCurrentUser()).thenReturn(user);

        accountService.closeAccount(account.getPublicId());

        var updatedAccount = accountRepository.findByPublicId(account.getPublicId()).orElseThrow();

        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(updatedAccount.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }
    @Test
    void closeAccount_shouldThrowExceptionIsNotYourAccount(){
        var user = createUser();
        var account = createAccount(user, Currency.RUB);

        var anotherUser = createUser();
        when(userService.getCurrentUser()).thenReturn(anotherUser);

        assertThatThrownBy(() -> accountService.closeAccount(account.getPublicId()))
                .isInstanceOf(RuntimeException.class);
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
    private Account createAccount(User user, Currency currency){
        return accountRepository.save(new Account(
                user, UUID.randomUUID().toString(), currency
        ));
    }
}
