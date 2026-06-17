package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.operation.MoneyOperationMapper;
import ru.khan.bank.operation.dto.DepositRequest;
import ru.khan.bank.operation.dto.MoneyOperationsResponse;
import ru.khan.bank.operation.dto.TransferRequest;
import ru.khan.bank.operation.dto.WithdrawRequest;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.repository.MoneyOperationRepository;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoneyOperationService {

    private final MoneyOperationCreator moneyOperationCreator;
    private final MoneyOperationExecutor moneyOperationExecutor;
    private final MoneyOperationRepository moneyOperationRepository;
    private final UserService userService;
    private final MoneyOperationMapper moneyOperationMapper;
    private final AccountService accountService;

    public void deposit(String idempotencyKey, DepositRequest request){

        Long operationId = moneyOperationCreator.createDeposit(idempotencyKey, request);

        moneyOperationExecutor.executeDeposit(operationId);

        findAndCheckStatus(operationId);
    }
    public void withdraw(String idempotencyKey, WithdrawRequest request){

        Long operationId = moneyOperationCreator.createWithdraw(idempotencyKey, request);

        moneyOperationExecutor.executeWithdraw(operationId);

        findAndCheckStatus(operationId);
    }

    public void transfers(String idempotencyKey, TransferRequest request){
        Long operationId = moneyOperationCreator.createTransfers(idempotencyKey, request);

        moneyOperationExecutor.executeTransfers(operationId);

        findAndCheckStatus(operationId);
    }

    public Page<MoneyOperationsResponse> getAllMyOperations(Integer size, Integer page, OperationsSort sort, Boolean asc) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        List<Long> accountsId = accountService.getAllMyAccounts(user.getId());

        if (accountsId.isEmpty())
            throw new RuntimeException("Your accounts not found");

        return moneyOperationRepository.findAllByFromAccountIdInOrToAccountIdIn(accountsId, accountsId, pageable)
                .map(operation -> moneyOperationMapper.toMoneyOperationsResponse(
                        operation.getPublicId(),
                        operation.getOperationNumber(),
                        operation.getType(),
                        operation.getStatus(),
                        operation.getFromAccountId(),
                        operation.getToAccountId(),
                        operation.getAmount(),
                        operation.getCurrency(),
                        operation.getCreatedAt(),
                        operation.getCompletedAt()
                ));
    }

    public Page<MoneyOperationsResponse> getMyOperationOnAccount(Integer size, Integer page, OperationsSort sort, Boolean asc, UUID accountPublicId) {
        User user = userService.getCurrentUser();

        Account account = accountService.getAccount(accountPublicId);

        if (!account.ensureIsOwner(user.getId()))
            throw new RuntimeException("You don't have access to perform this action");

        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return moneyOperationRepository.findAllByFromAccountIdOrToAccountId(user.getId(), user.getId(), pageable)
                .map(operation -> moneyOperationMapper.toMoneyOperationsResponse(
                        operation.getPublicId(),
                        operation.getOperationNumber(),
                        operation.getType(),
                        operation.getStatus(),
                        operation.getFromAccountId(),
                        operation.getToAccountId(),
                        operation.getAmount(),
                        operation.getCurrency(),
                        operation.getCreatedAt(),
                        operation.getCompletedAt()
                ));
    }

    public Page<OperationsPageableResponse> getOperations(Pageable pageable) {
        return moneyOperationRepository.findAll(pageable)
                .map(operation -> moneyOperationMapper.toOperationsPageable(
                        operation.getPublicId(),
                        operation.getType(),
                        operation.getStatus(),
                        operation.getFromAccountId(),
                        operation.getToAccountId(),
                        operation.getAmount(),
                        operation.getCurrency(),
                        operation.getCreatedAt(),
                        operation.getCompletedAt()
                        )
                );
    }

    private void findAndCheckStatus(Long operationId){
        var operation = moneyOperationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Operation not found"));

        if (operation.isCancelled()){
            throw new RuntimeException("Operation cancelled");
        } else if (operation.isFailed()){
            throw new RuntimeException("Operation failed");
        }
    }

}
