package ru.khan.bank.operation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.operation.dto.*;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.service.MoneyOperationService;

import java.util.UUID;

@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
public class MoneyOperationController {

    private final MoneyOperationService moneyOperationService;

    @PostMapping("/deposit")
    public OperationResponse deposit(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                     @Valid @RequestBody DepositRequest request) {

        return moneyOperationService.deposit(idempotencyKey, request);
    }
    @PostMapping("/withdraw")
    public OperationResponse withdraw(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                      @Valid @RequestBody WithdrawRequest request){

        return moneyOperationService.withdraw(idempotencyKey, request);
    }
    @PostMapping("/transfers")
    public OperationResponse transfers(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                       @Valid @RequestBody TransferRequest request){
        return moneyOperationService.transfers(idempotencyKey, request);
    }
    @GetMapping
    public Page<MoneyOperationsResponse> getOperations(@RequestParam(value = "size", defaultValue = "20") Integer size,
                                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "sort", defaultValue = "CREATED_AT") OperationsSort sort,
                                                       @RequestParam(value = "asc", defaultValue = "true") Boolean asc){
        return moneyOperationService.getAllMyOperations(size, page, sort, asc);
    }
    @GetMapping("/{accountPublicId}")
    public Page<MoneyOperationsResponse> getOperationsOnAccount(@RequestParam(value = "size", defaultValue = "20") Integer size,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "sort", defaultValue = "CREATED_AT") OperationsSort sort,
                                          @RequestParam(value = "asc", defaultValue = "true") Boolean asc,
                                          @PathVariable UUID accountPublicId) {
        return moneyOperationService.getMyOperationOnAccount(size, page, sort, asc, accountPublicId);
    }

}
