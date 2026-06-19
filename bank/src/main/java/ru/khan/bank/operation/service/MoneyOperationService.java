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
import ru.khan.bank.operation.dto.*;
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

    public OperationResponse deposit(String idempotencyKey, DepositRequest request) {
        Long operationId = moneyOperationCreator.createDeposit(idempotencyKey, request);

        try {
            moneyOperationExecutor.executeDeposit(operationId);
        } catch (Exception e) {
            var operation = moneyOperationRepository.findById(operationId).orElse(null);

            if (operation != null) {
                operation.fail(e.getMessage());
                moneyOperationRepository.save(operation);
            }
        }
        return checkStatus(idempotencyKey);
    }

    public OperationResponse withdraw(String idempotencyKey, WithdrawRequest request){
        Long operationId = moneyOperationCreator.createWithdraw(idempotencyKey, request);
        try {
            moneyOperationExecutor.executeWithdraw(operationId);
        } catch (Exception e) {
            var operation = moneyOperationRepository.findById(operationId).orElse(null);

            if (operation != null) {
                operation.fail(e.getMessage());
                moneyOperationRepository.save(operation);
            }
        }

        return checkStatus(idempotencyKey);
    }

    public OperationResponse transfers(String idempotencyKey, TransferRequest request){
        Long operationId = moneyOperationCreator.createTransfers(idempotencyKey, request);
        try {
            moneyOperationExecutor.executeTransfers(operationId);

        } catch (Exception e) {
            var operation = moneyOperationRepository.findById(operationId).orElse(null);

            if (operation != null) {
                operation.fail(e.getMessage());
                moneyOperationRepository.save(operation);
            }
        }

        return checkStatus(idempotencyKey);
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

    private OperationResponse checkStatus(String idempotencyKey){
        var operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new RuntimeException("Operation not found"));

        if (operation.isFailed() || operation.isCancelled())
            throw new RuntimeException("Operation failed");

        return new OperationResponse(operation.getStatus());
    }

}
