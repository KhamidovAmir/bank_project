package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.common.exception.exceptions.NotFoundException;
import ru.khan.bank.operation.entity.MoneyOperation;
import ru.khan.bank.operation.repository.MoneyOperationRepository;

@Service
@RequiredArgsConstructor
class MoneyOperationExecutor {

    private static final String ACCOUNT_NOT_FOUND = "Account not found";
    private static final String OPERATION_NOT_FOUND = "Operation not found";

    private final MoneyOperationRepository moneyOperationRepository ;
    private final AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeDeposit(Long operationId) {

        MoneyOperation operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException(OPERATION_NOT_FOUND));

        if (!operation.isPending()) return;

        Account account = accountRepository.findById(operation.getToAccountId())
                .orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

        account.deposit(operation.getAmount());
        operation.complete();

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeWithdraw(Long operationId) {

        MoneyOperation operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException(OPERATION_NOT_FOUND));

        if (!operation.isPending()) return;

        Account account = accountRepository.findById(operation.getFromAccountId())
                .orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

        account.withdraw(operation.getAmount());
        operation.complete();
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeTransfers(Long operationId) {

        MoneyOperation operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException(OPERATION_NOT_FOUND));

        if (!operation.isPending()) return;

        Account to = accountRepository.findById(operation.getToAccountId())
                .orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

        Account from = accountRepository.findById(operation.getFromAccountId())
                .orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

        from.withdraw(operation.getAmount());
        to.deposit(operation.getAmount());
        operation.complete();
    }

}
