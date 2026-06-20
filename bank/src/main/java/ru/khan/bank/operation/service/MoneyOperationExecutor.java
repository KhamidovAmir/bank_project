package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.operation.entity.MoneyOperation;
import ru.khan.bank.operation.repository.MoneyOperationRepository;

@Service
@RequiredArgsConstructor
class MoneyOperationExecutor {

    private final MoneyOperationRepository moneyOperationRepository ;
    private final AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeDeposit(Long operationId) {

        MoneyOperation operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Money operation not found"));

        if (!operation.isPending()) return;

        Account account = accountRepository.findById(operation.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.deposit(operation.getAmount());
        operation.complete();

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeWithdraw(Long operationId) {

        MoneyOperation operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Money operation not found"));

        if (!operation.isPending()) return;

        Account account = accountRepository.findById(operation.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.withdraw(operation.getAmount());
        operation.complete();
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeTransfers(Long operationId) {

        MoneyOperation operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Money operation not found"));

        if (!operation.isPending()) return;

        Account to = accountRepository.findById(operation.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Account from = accountRepository.findById(operation.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        from.withdraw(operation.getAmount());
        to.deposit(operation.getAmount());
        operation.complete();
    }

}
