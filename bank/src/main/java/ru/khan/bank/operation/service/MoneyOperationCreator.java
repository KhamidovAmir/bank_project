package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.operation.dto.DepositRequest;
import ru.khan.bank.operation.dto.TransferRequest;
import ru.khan.bank.operation.dto.WithdrawRequest;
import ru.khan.bank.operation.entity.MoneyOperation;
import ru.khan.bank.operation.entity.OperationType;
import ru.khan.bank.operation.repository.MoneyOperationRepository;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class MoneyOperationCreator {

    private final MoneyOperationRepository moneyOperationRepository;
    private final UserService userService;
    private final AccountService accountService;

    @Transactional
    public Long createDeposit(String idempotencyKey, DepositRequest request) {

        User user = userService.getCurrentUser();

        Account account = accountService.getAccount(request.accountPublicId());

        if (!account.ensureIsOwner(user.getId())) {
            throw new RuntimeException("You can't deposit not your account");
        }

        if (!account.ensureActive()) {
            throw new RuntimeException("This account is not active");
        }

        MoneyOperation operation = MoneyOperation.deposit(
                generateOperationNumber(),
                account.getId(),
                request.amount(),
                account.getCurrency(),
                request.description(),
                idempotencyKey
        );

        if (ensureIdempotencyKeyIsNotContains(idempotencyKey) != -1) {
            return ensureSameOperation(operation,
                    OperationType.DEPOSIT,
                    request.amount(),
                    Currency.RUB,
                    null,
                    account.getId());
        }

        try {
            return moneyOperationRepository.save(operation).getId();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Try operation after some time");
        }
    }

    @Transactional
    public Long createWithdraw(String idempotencyKey, WithdrawRequest request) {

        User user = userService.getCurrentUser();
        Account account = accountService.getAccount(request.accountPublicId());

        if (!account.ensureIsOwner(user.getId()))
            throw new RuntimeException("You don't have access to perform this action");

        if (!account.ensureActive())
            throw new RuntimeException("This account is not active");

        MoneyOperation operation = MoneyOperation.withdraw(
                generateOperationNumber(),
                account.getId(),
                request.amount(),
                account.getCurrency(),
                request.description(),
                idempotencyKey
        );

        try {
            return moneyOperationRepository.save(operation).getId();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Try operation after some time");
        }
    }

    @Transactional
    public Long createTransfers(String idempotencyKey, TransferRequest request) {

        User user = userService.getCurrentUser();

        Account from = accountService.getAccount(request.accountPublicIdFrom());
        Account to = accountService.getAccount(request.accountPublicIdTo());

        if (!from.ensureIsOwner(user.getId()))
            throw new RuntimeException("You don't have access to perform this action");
        if (!from.ensureActive())
            throw new RuntimeException("Your account is not active");
        if (!to.ensureActive())
            throw new RuntimeException("Account you try transfer is not active");
        if (from.getId().equals(to.getId()))
            throw new RuntimeException("You try transfer the same account");
        if (from.getCurrency() != to.getCurrency())
            throw new RuntimeException("Currencies are not equal");

        MoneyOperation operation = MoneyOperation.transfer(
                generateOperationNumber(),
                from.getId(),
                to.getId(),
                request.amount(),
                from.getCurrency(),
                request.description(),
                idempotencyKey
        );

        try {
            return moneyOperationRepository.save(operation).getId();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Try operation after some time");
        }
    }

    private String generateOperationNumber() {
        return "OP-000" + UUID.randomUUID();
    }

    private Long ensureIdempotencyKeyIsNotContains(String idempotencyKey) {
        var operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (operation == null) return -1L;

        return operation.getId();
    }

    private Long ensureSameOperation(MoneyOperation operation, OperationType type,
                                     BigDecimal amount, Currency currency,
                                     Long idFrom, Long idTo) {
        boolean same = operation.getType() == type
                && operation.getAmount().compareTo(amount) == 0
                && operation.getCurrency() == currency
                && Objects.equals(operation.getFromAccountId(), idFrom)
                && Objects.equals(operation.getToAccountId(), idTo);

        if (!same)
            throw new RuntimeException("Idempotency key was already used");

        return operation.getId();
    }
}

