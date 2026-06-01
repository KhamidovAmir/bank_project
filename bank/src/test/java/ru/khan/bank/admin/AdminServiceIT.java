package ru.khan.bank.admin;

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
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.account.entity.AccountStatus;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.admin.service.AdminService;
import ru.khan.bank.auth.dto.JwtUser;
import ru.khan.bank.auth.service.AuthUserProvider;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserSort;
import ru.khan.bank.user.entity.UserStatus;
import ru.khan.bank.user.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class AdminServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @MockitoBean
    private AuthUserProvider  authUserProvider;

    @AfterEach
    void cleanDb(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getUsers_shouldReturnPageOfUsers(){
        var user1 = createUser();
        var user2 = createUser();
        var user3 = createUser();

        var admin = createAdmin();

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(admin));

        var result = adminService.getUsers(10,0, UserSort.CREATED_AT, true);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result).hasSize(4);
    }

    @Test
    void getUsers_shouldThrowExceptionNotAdmin(){
        var user1 = createUser();
        var user2 = createUser();
        var user3 = createUser();

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(user1));

        assertThatThrownBy(() -> adminService.getUsers(10,0, UserSort.CREATED_AT, true))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    void getAccounts_shouldReturnPageOfUsers(){
        var user1 = createUser();
        var user2 = createUser();
        var user3 = createUser();

        var account1 = createAccount(user1);
        var account2 = createAccount(user2);
        var account3 = createAccount(user3);

        var admin = createAdmin();

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(admin));

        var result = adminService.getAccounts(10,0, AccountSort.CREATED_AT, true);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result).hasSize(3);
    }


    @Test
    void getAccounts_shouldThrowExceptionNotAdmin(){
        var user1 = createUser();
        var user2 = createUser();
        var user3 = createUser();

        var account1 = createAccount(user1);
        var account2 = createAccount(user2);
        var account3 = createAccount(user3);

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(user1));

        assertThatThrownBy(() -> adminService.getAccounts(10,0, AccountSort.CREATED_AT, true))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void unblockAccount_shouldUnblockAccount(){
        var user = createUser();
        var admin = createAdmin();

        var account = createAccount(user);
        account.block();
        accountRepository.save(account);

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(admin));

        assertThat(account.getStatus()).isEqualTo(AccountStatus.BLOCKED);

        adminService.unblockAccount(account.getPublicId());

        var updatedAccount = accountRepository.findByPublicId(account.getPublicId()).orElseThrow();

        assertThat(updatedAccount.getId()).isEqualTo(account.getId());
        assertThat(updatedAccount.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
    @Test
    void unblockAccount_shouldThrowExceptionNotAdmin(){
        var user = createUser();

        var user2 = createUser() ;
        var account = createAccount(user);
        account.block();
        accountRepository.save(account);

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(user2));

        assertThatThrownBy(() -> adminService.unblockAccount(account.getPublicId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void blockAccount_shouldUnblockAccount(){
        var user = createUser();
        var admin = createAdmin();

        var account = createAccount(user);

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(admin));

        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        adminService.blockAccount(account.getPublicId());

        var updatedAccount = accountRepository.findByPublicId(account.getPublicId()).orElseThrow();

        assertThat(updatedAccount.getId()).isEqualTo(account.getId());
        assertThat(updatedAccount.getStatus()).isEqualTo(AccountStatus.BLOCKED);
    }

    @Test
    void blockAccount_shouldThrowExceptionNotAdmin(){
        var user2 = createUser();
        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(user2));

        var user = createUser();
        var account = createAccount(user);

        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        assertThatThrownBy(() -> adminService.blockAccount(account.getPublicId()))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    void unblockUser_shouldUnblockAccount(){
        var user = createUser();
        var admin = createAdmin();

        user.block();
        userRepository.save(user);

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(admin));

        assertThat(user.getStatus()).isEqualTo(UserStatus.BLOCKED);

        adminService.unblockUser(user.getPublicId());

        var updatedUser = userRepository.findByPublicId(user.getPublicId()).orElseThrow();

        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void unblockUser_shouldThrowExceptionNotAdmin(){
        var user = createUser();

        var user2 = createUser();

        user.block();
        userRepository.save(user);

        when(authUserProvider.getCurrentUser()).thenReturn(returnJwtUser(user2));

        assertThat(user.getStatus()).isEqualTo(UserStatus.BLOCKED);

        assertThatThrownBy(() -> adminService.unblockAccount(user.getPublicId()))
                .isInstanceOf(RuntimeException.class);
    }


    private JwtUser returnJwtUser(User user){
        return new JwtUser(user.getPublicId(),
                user.getEmail(),
                user.getRole().toString());
    }

    private Account createAccount(User user){
        return accountRepository.save(new Account(user, UUID.randomUUID().toString(), Currency.RUB));
    }

    private User createUser(){
        return userRepository.save(
                new User(UUID.randomUUID()  + "@gmail.com",
                        "password",
                        "test",
                        "test",
                        UserRole.CUSTOMER)
        );
    }

    private User createAdmin(){
        return userRepository.save(
                new User("test@gmail.com",
                        "password",
                        "test",
                        "test",
                        UserRole.ADMIN)
        );
    }
}
