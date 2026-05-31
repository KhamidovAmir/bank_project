package ru.khan.bank.moneyoperation;

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
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.operation.dto.DepositRequest;
import ru.khan.bank.operation.dto.TransferRequest;
import ru.khan.bank.operation.dto.WithdrawRequest;
import ru.khan.bank.operation.entity.MoneyOperation;
import ru.khan.bank.operation.entity.OperationStatus;
import ru.khan.bank.operation.entity.OperationType;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.repository.MoneyOperationRepository;
import ru.khan.bank.operation.service.MoneyOperationService;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.repository.UserRepository;
import ru.khan.bank.user.service.UserService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class MoneyOperationServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private MoneyOperationService moneyOperationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MoneyOperationRepository moneyOperationRepository;

    @MockitoBean
    private UserService userService;

    @AfterEach
    void cleanDb() {
        moneyOperationRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void deposit_shouldIncreaseAccountBalanceAndCreateMoneyOperation() {

        User user = createUser();

        Account account = createAccount(user, Currency.RUB);

        when(userService.getCurrentUser()).thenReturn(user);

        DepositRequest request = new DepositRequest(
                account.getPublicId(),
                new BigDecimal("100.00"),
                "test");

        String idempotencyKey = "deposit-" + UUID.randomUUID();

        moneyOperationService.deposit(idempotencyKey, request);

        Account updateAccount = accountRepository.findByPublicId(account.getPublicId())
                .orElseThrow();

        assertThat(updateAccount.
                getBalance()).
                isEqualByComparingTo(new BigDecimal("100.00"));

        MoneyOperation operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();

        assertThat(operation).isNotNull();

        assertThat(operation.getAmount())
                .isEqualByComparingTo("100.00");

        assertThat(operation.getToAccountId())
                .isEqualTo(account.getId());

        assertThat(operation.getCurrency())
                .isEqualTo(Currency.RUB);

        assertThat(operation.getIdempotencyKey())
                .isNotBlank();
    }

    @Test
    void withdraw_shouldReduceAccountBalanceAndCreateMoneyOperation() {

        User user = createUser();

        Account account = createAccount(user, Currency.RUB);

        account.deposit(new BigDecimal("20.00"));
        account = accountRepository.save(account);

        when(userService.getCurrentUser()).thenReturn(user);

        WithdrawRequest request = new WithdrawRequest(
                account.getPublicId(),
                new BigDecimal("10.00"),
                "test"
        );

        String idempotencyKey = "withdraw-" + UUID.randomUUID();

        moneyOperationService.withdraw(idempotencyKey, request);

        Account updateAccount = accountRepository.findByPublicId(account.getPublicId())
                .orElseThrow();

        assertThat(updateAccount.
                getBalance()).
                isEqualByComparingTo(new BigDecimal("10.00"));

        MoneyOperation operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();

        assertThat(operation).isNotNull();

        assertThat(operation.getAmount())
                .isEqualByComparingTo("10.00");

        assertThat(operation.getFromAccountId())
                .isEqualTo(account.getId());

        assertThat(operation.getCurrency())
                .isEqualTo(Currency.RUB);

        assertThat(operation.getIdempotencyKey())
                .isNotBlank();

    }

    @Test
    void transfers_shouldTransferAccountBalanceAndCreateMoneyOperation() {
        User firstUser = createUser();
        User secondUser = createUser();

        Account firstAccount = createAccount(firstUser, Currency.RUB);
        Account secondAccount = createAccount(secondUser, Currency.RUB);

        firstAccount.deposit(new BigDecimal("20.00"));
        firstAccount = accountRepository.save(firstAccount);

        when(userService.getCurrentUser()).thenReturn(firstUser);

        TransferRequest request = new TransferRequest(
                firstAccount.getPublicId(),
                secondAccount.getPublicId(),
                new BigDecimal("10.00"),
                "test"
                );

        String idempotencyKey = "transfer-" + UUID.randomUUID();

        moneyOperationService.transfers(idempotencyKey, request);

        firstAccount = accountRepository.findByPublicId(firstAccount.getPublicId()).orElseThrow();
        secondAccount = accountRepository.findByPublicId(secondAccount.getPublicId()).orElseThrow();

        MoneyOperation operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();

        assertThat(operation).isNotNull();

        assertThat(operation.getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));

        assertThat(operation.getFromAccountId()).isEqualTo(firstAccount.getId());
        assertThat(operation.getToAccountId()).isEqualTo(secondAccount.getId());

        assertThat(operation.getType()).isEqualTo(OperationType.TRANSFER);
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.COMPLETED);

        assertThat(firstAccount.getBalance()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(secondAccount.getBalance()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void transfer_shouldThrowExceptionInsufficientFundsAndCreateMoneyOperation(){

        User firstUser = createUser();
        User secondUser = createUser();

        Account firstAccount = createAccount(firstUser, Currency.RUB);
        Account secondAccount = createAccount(secondUser, Currency.RUB);

        when(userService.getCurrentUser()).thenReturn(firstUser);

        TransferRequest request = new TransferRequest(
                firstAccount.getPublicId(),
                secondAccount.getPublicId(),
                new BigDecimal("10.00"),
                "test"
        );

        String idempotencyKey = "transfer-" + UUID.randomUUID();

        moneyOperationService.transfers(idempotencyKey, request);

        firstAccount = accountRepository.findByPublicId(firstAccount.getPublicId()).orElseThrow();
        secondAccount = accountRepository.findByPublicId(secondAccount.getPublicId()).orElseThrow();

        MoneyOperation operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();

        assertThat(operation).isNotNull();

        assertThat(operation.getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));

        assertThat(operation.getFromAccountId()).isEqualTo(firstAccount.getId());
        assertThat(operation.getToAccountId()).isEqualTo(secondAccount.getId());

        assertThat(operation.getType()).isEqualTo(OperationType.TRANSFER);
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.FAILED);

        assertThat(firstAccount.getBalance()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(secondAccount.getBalance()).isEqualByComparingTo(new BigDecimal("0.00"));

    }

    @Test
    void transfer_shouldCompleteFirstRequestAndRejectSecondRequestWithSameIdempotencyKey(){

        User firstUser = createUser();
        User secondUser = createUser();

        Account firstAccount = createAccount(firstUser, Currency.RUB);
        Account secondAccount = createAccount(secondUser, Currency.RUB);

        firstAccount.deposit(new BigDecimal("20.00"));
        firstAccount = accountRepository.save(firstAccount);

        when(userService.getCurrentUser()).thenReturn(firstUser);

        TransferRequest request = new TransferRequest(
                firstAccount.getPublicId(),
                secondAccount.getPublicId(),
                new BigDecimal("10.00"),
                "test"
        );

        String idempotencyKey = "transfer-" + UUID.randomUUID();

        moneyOperationService.transfers(idempotencyKey, request);

        assertThatThrownBy(() ->
                moneyOperationService.transfers(idempotencyKey, request))
                .isInstanceOf(RuntimeException.class);

        firstAccount = accountRepository.findByPublicId(firstAccount.getPublicId()).orElseThrow();
        secondAccount = accountRepository.findByPublicId(secondAccount.getPublicId()).orElseThrow();

        MoneyOperation operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();

        assertThat(operation).isNotNull();

        assertThat(moneyOperationRepository.countByIdempotencyKey(idempotencyKey))
                .isEqualTo(1);

        assertThat(operation.getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));

        assertThat(operation.getFromAccountId()).isEqualTo(firstAccount.getId());
        assertThat(operation.getToAccountId()).isEqualTo(secondAccount.getId());

        assertThat(operation.getType()).isEqualTo(OperationType.TRANSFER);
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.COMPLETED);

        assertThat(firstAccount.getBalance()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(secondAccount.getBalance()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void getAllMyOperations_shouldReturnPaginationResult(){
        User user = createUser();

        Account firstAccount = createAccount(user, Currency.RUB);
        Account secondAccount = createAccount(user, Currency.RUB);

        DepositRequest firstRequest = new DepositRequest(firstAccount.getPublicId(), new BigDecimal("20.00"), null);
        DepositRequest secondRequest = new DepositRequest(secondAccount.getPublicId(), new BigDecimal("20.00"), null);

        when(userService.getCurrentUser()).thenReturn(user);

        String firstIdempotencyKey = "deposit-" + UUID.randomUUID();
        String secondIdempotencyKey = "deposit-" + UUID.randomUUID();


        moneyOperationService.deposit(firstIdempotencyKey, firstRequest);
        moneyOperationService.deposit(secondIdempotencyKey, secondRequest);

        var firstPage = moneyOperationService.getAllMyOperations(1, 0, OperationsSort.OPERATIONS_TYPE, true);
        var secondPage = moneyOperationService.getAllMyOperations(1, 1, OperationsSort.OPERATIONS_TYPE, true);

        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).hasSize(1);

        assertThat(secondPage.getContent()).hasSize(1);

    }

    @Test
    void getAllMyOperations_shouldReturnOnlyCurrentUserOperations(){
        /*
            Another user and his operations
        */
        User anotherUser = createUser();

        Account accountForAnotherUser = createAccount(anotherUser, Currency.RUB);
        DepositRequest anotherRequest = new DepositRequest(accountForAnotherUser.getPublicId(), new BigDecimal("50.00"), null);

        when(userService.getCurrentUser()).thenReturn(anotherUser);

        String anotherIdempotencyKey = "deposit-" + UUID.randomUUID();
        moneyOperationService.deposit(anotherIdempotencyKey, anotherRequest);
        /*
            Current user and his operations
        */
        User currentUser = createUser();

        Account firstAccount = createAccount(currentUser, Currency.RUB);
        Account secondAccount = createAccount(currentUser, Currency.RUB);

        DepositRequest firstRequest = new DepositRequest(firstAccount.getPublicId(), new BigDecimal("20.00"), null);
        DepositRequest secondRequest = new DepositRequest(secondAccount.getPublicId(), new BigDecimal("20.00"), null);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        String firstIdempotencyKey = "deposit-" + UUID.randomUUID();
        String secondIdempotencyKey = "deposit-" + UUID.randomUUID();

        moneyOperationService.deposit(firstIdempotencyKey, firstRequest);
        moneyOperationService.deposit(secondIdempotencyKey, secondRequest);

        var result = moneyOperationService.getAllMyOperations(10, 0, OperationsSort.OPERATIONS_TYPE, true);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getContent())
                .allMatch(operation ->
                        (operation.toAccountId().equals(firstAccount.getId())) ||
                                (operation.toAccountId().equals(secondAccount.getId())) &&
                                        (operation.type().equals(OperationType.DEPOSIT))

                );
    }


    @Test
    void getMyOperationOnAccount_shouldReturnOperationForOneAccount(){
        User user = createUser();

        Account firstAccount = createAccount(user, Currency.RUB);
        Account secondAccount = createAccount(user, Currency.RUB);

        DepositRequest firstRequest = new DepositRequest(firstAccount.getPublicId(), new BigDecimal("50.00"), null);
        DepositRequest secondRequest = new DepositRequest(secondAccount.getPublicId(), new BigDecimal("30.00"), null);

        when(userService.getCurrentUser()).thenReturn(user);

        String firstIdempotencyKey = "deposit-" + UUID.randomUUID();
        String secondIdempotencyKey = "deposit-" + UUID.randomUUID();

        moneyOperationService.deposit(firstIdempotencyKey, firstRequest);
        moneyOperationService.deposit(secondIdempotencyKey, secondRequest);

        var result = moneyOperationService.getMyOperationOnAccount(10,0,OperationsSort.OPERATIONS_TYPE, true, firstAccount.getPublicId());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        assertThat(result.getContent())
                .allMatch(operation -> operation.toAccountId().equals(firstAccount.getId()));

    }

    private User createUser(){
        return userRepository.save(
                new User("test-" + UUID.randomUUID() + "@gmail.com",
                        "password",
                        "test",
                        "test",
                        UserRole.CUSTOMER
                )
        );
    }
    private Account createAccount(User user, Currency currency){
        return accountRepository.save(
                new Account(
                user,
                "number" + UUID.randomUUID(),
                currency
                )
        );
    }
}
