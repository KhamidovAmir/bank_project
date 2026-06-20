package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.Currency;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.common.exception.exceptions.AccessDeniedException;
import ru.khan.bank.common.exception.exceptions.BadRequestException;
import ru.khan.bank.common.exception.exceptions.ConflictException;
import ru.khan.bank.common.exception.exceptions.IdempotencyConflictException;
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
import java.util.Optional;
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
            throw new AccessDeniedException("You can't deposit not your account");
        }

        if (!account.ensureActive()) {
            throw new ConflictException("This account is not active");
        }

        Optional<MoneyOperation> existing = moneyOperationRepository
                .findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            ensureSameOperation(existing.get(),
                    OperationType.DEPOSIT,
                    request.amount(),
                    account.getCurrency(),
                    null,
                    account.getId());
            return existing.get().getId();
        }

        MoneyOperation operation = MoneyOperation.deposit(
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
            MoneyOperation concurrent = moneyOperationRepository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> e);
            ensureSameOperation(concurrent, OperationType.DEPOSIT,
                    request.amount(), account.getCurrency(),
                    null, account.getId());
            return concurrent.getId();
        }
    }

    @Transactional
    public Long createWithdraw(String idempotencyKey, WithdrawRequest request) {

        User user = userService.getCurrentUser();
        Account account = accountService.getAccount(request.accountPublicId());

        if (!account.ensureIsOwner(user.getId()))
            throw new AccessDeniedException("You don't have access to perform this action");

        if (!account.ensureActive())
            throw new ConflictException("This account is not active");

        Optional<MoneyOperation> existing = moneyOperationRepository
                .findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            ensureSameOperation(existing.get(),
                    OperationType.WITHDRAW,
                    request.amount(),
                    account.getCurrency(),
                    account.getId(),
                    null);

            return existing.get().getId();
        }

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
            MoneyOperation concurrent = moneyOperationRepository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> e);
            ensureSameOperation(concurrent, OperationType.WITHDRAW,
                    request.amount(), account.getCurrency(),
                    account.getId(), null);
            return concurrent.getId();
        }
    }

    @Transactional
    public Long createTransfers(String idempotencyKey, TransferRequest request) {

        User user = userService.getCurrentUser();

        Account from = accountService.getAccount(request.accountPublicIdFrom());
        Account to = accountService.getAccount(request.accountPublicIdTo());

        if (!from.ensureIsOwner(user.getId()))
            throw new AccessDeniedException("You don't have access to perform this action");
        if (!from.ensureActive())
            throw new ConflictException("Your account is not active");
        if (!to.ensureActive())
            throw new ConflictException("Account you try transfer is not active");
        if (from.getId().equals(to.getId()))
            throw new BadRequestException("You try transfer the same account");
        if (from.getCurrency() != to.getCurrency())
            throw new ConflictException("Currencies are not equal");

        Optional<MoneyOperation> existing = moneyOperationRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            ensureSameOperation(existing.get(),
                    OperationType.TRANSFER,
                    request.amount(),
                    from.getCurrency(),
                    from.getId(),
                    to.getId());

            return existing.get().getId();
        }

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
            MoneyOperation concurrent = moneyOperationRepository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> e);
            ensureSameOperation(concurrent, OperationType.TRANSFER,
                    request.amount(), to.getCurrency(),
                    from.getId(), to.getId());
            return concurrent.getId();
        }
    }

    private String generateOperationNumber() {
        return "OP-000" + UUID.randomUUID();
    }

    private void ensureSameOperation(MoneyOperation operation, OperationType type,
                                     BigDecimal amount, Currency currency,
                                     Long idFrom, Long idTo) {
        boolean same = operation.getType() == type
                && operation.getAmount().compareTo(amount) == 0
                && operation.getCurrency() == currency
                && Objects.equals(operation.getFromAccountId(), idFrom)
                && Objects.equals(operation.getToAccountId(), idTo);

        if (!same)
            throw new IdempotencyConflictException("Idempotency key was already used");
    }
}

