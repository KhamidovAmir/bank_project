package ru.khan.bank.operation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.operation.dto.DepositRequest;
import ru.khan.bank.operation.dto.MoneyOperationsResponse;
import ru.khan.bank.operation.dto.TransferRequest;
import ru.khan.bank.operation.dto.WithdrawRequest;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.service.MoneyOperationService;

import java.util.UUID;

@RestController
@RequestMapping("/operations")
public class MoneyOperationController {

    private final MoneyOperationService moneyOperationService;

    public MoneyOperationController(MoneyOperationService moneyOperationService) {
        this.moneyOperationService = moneyOperationService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                        @RequestBody DepositRequest request) {
        moneyOperationService.deposit(idempotencyKey, request);
        return ResponseEntity.status(201).build();
    }
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                         @RequestBody WithdrawRequest request){
        moneyOperationService.withdraw(idempotencyKey, request);
        return ResponseEntity.status(201).build();
    }
    @PostMapping("/transfers")
    public ResponseEntity<Void> transfers(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                         @RequestBody TransferRequest request){
        moneyOperationService.transfers(idempotencyKey, request);
        return ResponseEntity.status(201).build();
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
