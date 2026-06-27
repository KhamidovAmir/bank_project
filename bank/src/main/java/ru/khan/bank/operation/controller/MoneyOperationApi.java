package ru.khan.bank.operation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.operation.dto.*;
import ru.khan.bank.operation.entity.OperationsSort;

import java.util.UUID;

@RequestMapping("/operations")
@Tag(name = "Контроллер работы с операциями", description = "Контроллер контура работы с операциями")
public interface MoneyOperationApi {

    @PostMapping("/deposit")
    @Operation(summary = "Операция пополнения счета")
    OperationResponse deposit(@RequestHeader("Idempotency-Key")
                              @Parameter( description = "Уникальный ключ идемпотентности. Повторные запросы с одинаковым ключом не приводят к повторному выполнению перевода.", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                              String idempotencyKey,
                              @Valid @RequestBody DepositRequest request);

    @PostMapping("/withdraw")
    @Operation(summary = "Операция вывода средств со счета")
    OperationResponse withdraw(@RequestHeader("Idempotency-Key")
                               @Parameter( description = "Уникальный ключ идемпотентности. Повторные запросы с одинаковым ключом не приводят к повторному выполнению перевода.", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                               String idempotencyKey,
                               @Valid @RequestBody WithdrawRequest request);

    @PostMapping("/transfers")
    @Operation(summary = "Операция перевода средств между счетами")
    OperationResponse transfers(@RequestHeader("Idempotency-Key")
                                @Parameter( description = "Уникальный ключ идемпотентности. Повторные запросы с одинаковым ключом не приводят к повторному выполнению перевода.", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                String idempotencyKey,
                                @Valid @RequestBody TransferRequest request);


    @GetMapping
    @Operation(summary = "Получение операций самого пользователя при помощи пагинации")
    Page<MoneyOperationsResponse> getOperations(@RequestParam(value = "size", defaultValue = "20")
                                                @Parameter(description = "Размер страницы - пагинации", example = "20", required = true)
                                                Integer size,

                                                @RequestParam(value = "page", defaultValue = "0")
                                                @Parameter(description = "Номер страницы", example = "0", required = true)
                                                Integer page,

                                                @RequestParam(value = "sort", defaultValue = "CREATED_AT")
                                                @Parameter(description = "Сортировка по полю", example = "CREATED_AT", required = true)
                                                OperationsSort sort,

                                                @RequestParam(value = "asc", defaultValue = "true")
                                                @Parameter(description = "Сортировка по направлению: true — по возрастанию, false — по убыванию", example = "true", required = true)
                                                Boolean asc);


    @GetMapping("/{accountPublicId}")
    @Operation(summary = "Получение операций по конкретному счета пользователя при помощи пагинации")
    Page<MoneyOperationsResponse> getOperationsOnAccount(@RequestParam(value = "size", defaultValue = "20")
                                                         @Parameter(description = "Размер страницы - пагинации", example = "20", required = true)
                                                         Integer size,

                                                         @RequestParam(value = "page", defaultValue = "0")
                                                         @Parameter(description = "Номер страницы", example = "0", required = true)
                                                         Integer page,

                                                         @RequestParam(value = "sort", defaultValue = "CREATED_AT")
                                                         @Parameter(description = "Сортировка по полю", example = "CREATED_AT", required = true)
                                                         OperationsSort sort,

                                                         @RequestParam(value = "asc", defaultValue = "true")
                                                         @Parameter(description = "Сортировка по направлению: true — по возрастанию, false — по убыванию", example = "true", required = true)
                                                         Boolean asc,

                                                         @PathVariable
                                                         @Parameter(description = "ID счета формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92", required = true)
                                                         UUID accountPublicId);

}
