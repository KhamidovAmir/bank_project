package ru.khan.bank.operation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.operation.dto.*;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.service.MoneyOperationService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MoneyOperationController implements MoneyOperationApi {

    private final MoneyOperationService moneyOperationService;

    public OperationResponse deposit(String idempotencyKey,
                                     DepositRequest request) {
        return moneyOperationService.deposit(idempotencyKey, request);
    }

    public OperationResponse withdraw(String idempotencyKey,
                                      WithdrawRequest request){

        return moneyOperationService.withdraw(idempotencyKey, request);
    }

    public OperationResponse transfers(String idempotencyKey,
                                       TransferRequest request){
        return moneyOperationService.transfers(idempotencyKey, request);
    }

    public Page<MoneyOperationsResponse> getOperations(Integer size,
                                                       Integer page,
                                                       OperationsSort sort,
                                                       Boolean asc){
        return moneyOperationService.getAllMyOperations(size, page, sort, asc);
    }

    public Page<MoneyOperationsResponse> getOperationsOnAccount(Integer size,
                                                                Integer page,
                                                                OperationsSort sort,
                                                                Boolean asc,
                                                                UUID accountPublicId) {
        return moneyOperationService.getMyOperationOnAccount(size, page, sort, asc, accountPublicId);
    }

}
