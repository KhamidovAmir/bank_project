package ru.khan.bank.operation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.common.exception.exceptions.AccessDeniedException;
import ru.khan.bank.common.exception.exceptions.ConflictException;
import ru.khan.bank.common.exception.exceptions.NotFoundException;
import ru.khan.bank.operation.mapper.MoneyOperationMapper;
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
        } catch (NotFoundException | ConflictException  e) {
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
            return Page.empty();

        return moneyOperationRepository.findAllByFromAccountIdInOrToAccountIdIn(accountsId, accountsId, pageable)
                .map(moneyOperationMapper::toMoneyOperationsResponse);
    }

    public Page<MoneyOperationsResponse> getMyOperationOnAccount(Integer size, Integer page, OperationsSort sort, Boolean asc, UUID accountPublicId) {
        User user = userService.getCurrentUser();

        Account account = accountService.getAccount(accountPublicId);

        if (!account.ensureIsOwner(user.getId()))
            throw new AccessDeniedException("You don't have access to perform this action");
        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return moneyOperationRepository.findAllByFromAccountIdOrToAccountId(account.getId(), account.getId(), pageable)
                .map(moneyOperationMapper::toMoneyOperationsResponse);
    }

    public Page<OperationsPageableResponse> getOperations(Pageable pageable) {
        return moneyOperationRepository.findAll(pageable)
                .map(moneyOperationMapper::toOperationsPageableResponse);
    }

    private OperationResponse checkStatus(String idempotencyKey){
        var operation = moneyOperationRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new NotFoundException("Operation not found"));

        if (operation.isFailed() || operation.isCancelled())
            throw new ConflictException(operation.getFailureReason());

        return new OperationResponse(operation.getStatus());
    }

}
