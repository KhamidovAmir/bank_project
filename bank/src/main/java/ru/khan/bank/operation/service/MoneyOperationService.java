package ru.khan.bank.operation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.operation.dto.DepositRequest;
import ru.khan.bank.operation.dto.WithdrawRequest;
import ru.khan.bank.operation.entity.MoneyOperation;
import ru.khan.bank.operation.repository.MoneyOperationRepository;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
public class MoneyOperationService {

    private final MoneyOperationRepository moneyOperationRepository;
    private final UserService userService;
    private final AccountService accountService;

    public MoneyOperationService(MoneyOperationRepository moneyOperationRepository, UserService userService, AccountService accountService) {
        this.moneyOperationRepository = moneyOperationRepository;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Transactional
    public void deposit(String idempotencyKey, DepositRequest request) {
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

        moneyOperationRepository.save(operation);

        try {
            account.deposit(request.amount());
            operation.complete();
        } catch(RuntimeException e) {
            operation.fail(e.getMessage());
            throw e;
        }

    }
    
    @Transactional
    public void withdraw(String idempotencyKey, WithdrawRequest request) {
        ensureIdempotencyKeyIsNotUsed(idempotencyKey);

        User user = userService.getCurrentUser();
        Account account = accountService.getAccount(request.accountPublicId());

        account.ensureIsOwner(user.getId());
        MoneyOperation operation = MoneyOperation.withdraw(
                generateOperationNumber(),
                account.getId(),
                request.amount(),
                account.getCurrency(),
                request.description(),
                idempotencyKey
        );
        moneyOperationRepository.save(operation);
        try {
            account.withdraw(request.amount());
            operation.complete();
        } catch(RuntimeException e) {
            operation.fail(e.getMessage());
            throw e;
        }
    }
    private String generateOperationNumber(){
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
