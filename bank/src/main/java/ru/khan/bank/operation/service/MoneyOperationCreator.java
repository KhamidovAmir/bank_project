package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.operation.MoneyOperationMapper;
import ru.khan.bank.operation.dto.DepositRequest;
import ru.khan.bank.operation.dto.MoneyOperationsResponse;
import ru.khan.bank.operation.dto.TransferRequest;
import ru.khan.bank.operation.dto.WithdrawRequest;
import ru.khan.bank.operation.entity.MoneyOperation;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.repository.MoneyOperationRepository;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class MoneyOperationCreator {

    private final MoneyOperationRepository moneyOperationRepository;
    private final UserService userService;
    private final AccountService accountService;
    private final MoneyOperationMapper moneyOperationMapper;

    @Transactional
    public Long createDeposit(String idempotencyKey, DepositRequest request) {
        ensureIdempotencyKeyIsNotUsed(idempotencyKey);

        User user = userService.getCurrentUser();

        Account account = accountService.getAccount(request.accountPublicId());

        if (!account.ensureIsOwner(user.getId()))
            throw new RuntimeException("You can't deposit not your account");

        if (!account.ensureActive())
            throw new RuntimeException("This account is not active");

        MoneyOperation operation = MoneyOperation.deposit(
                generateOperationNumber(),
                account.getId(),
                request.amount(),
                account.getCurrency(),
                request.description(),
                idempotencyKey
        );

        var saved = moneyOperationRepository.save(operation);

        return saved.getId();
    }

    @Transactional
    public Long createWithdraw(String idempotencyKey, WithdrawRequest request) {
        ensureIdempotencyKeyIsNotUsed(idempotencyKey);

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

        var saved = moneyOperationRepository.save(operation);
        return saved.getId();
    }

    @Transactional
    public Long createTransfers(String idempotencyKey, TransferRequest request) {
        ensureIdempotencyKeyIsNotUsed(idempotencyKey);

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

        var saved = moneyOperationRepository.save(operation);
        return saved.getId();
    }

    private String generateOperationNumber() {
        return "OP-000" + UUID.randomUUID();
    }

    private void ensureIdempotencyKeyIsNotUsed(String idempotencyKey) {

        Optional<MoneyOperation> existingOperation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey);

        existingOperation.ifPresent(operation -> {
            if (operation.isPending()) {
                throw new RuntimeException("Operation is already processing");
            }

            if (operation.isCompleted()) {
                throw new RuntimeException("Operation was already completed");
            }

            if (operation.isFailed()) {
                throw new RuntimeException("Operation was already failed");
            }

            if (operation.isCancelled()) {
                throw new RuntimeException("Operation was already cancelled");
            }
        });
    }
}

