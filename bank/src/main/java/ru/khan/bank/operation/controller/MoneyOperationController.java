package ru.khan.bank.operation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.operation.DepositRequest;
import ru.khan.bank.operation.service.MoneyOperationService;

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
        return  ResponseEntity.status(201).build();
    }
}
