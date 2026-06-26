package ru.khan.bank.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.account.dto.AccountPageResponse;
import ru.khan.bank.account.dto.AccountResponse;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.AccountSort;

import java.util.UUID;

@Tag(
        name = "Контроллер контура счетов",
        description = "Контур отвечает за работу с пользовательскими счетами в банке"
)
@RequestMapping("/accounts")
public interface AccountApi {

    @PostMapping
    @Operation(
            summary = "Создание счета",
            description = "Запрос на создание счета с определенной валютой, тело запроса обязательна"
    )
    ResponseEntity<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest);

    @Operation(
            summary = "Получение всех счетов пользователя",
            description = "Получение всех счетов пользователя посредством пагинации, с возможность фильтрации"
    )
    @GetMapping("/my")
    Page<AccountPageResponse> getMyAccounts
            (
                    @RequestParam(value = "size", defaultValue = "20")
                    @Parameter(description = "Размер страницы - пагинации", example = "20", required = true)
                    Integer size,

                    @RequestParam(value = "page", defaultValue = "0")
                    @Parameter(description = "Номер страницы", example = "0", required = true)
                    Integer page,

                    @RequestParam(value = "sort", defaultValue = "CREATED_AT")
                    @Parameter(description = "Сортировка по полю", example = "CREATED_AT", required = true)
                    AccountSort sort,

                    @RequestParam(value = "asc", defaultValue = "true")
                    @Parameter(description = "Сортировка по направлению: true — по возрастанию, false — по убыванию", example = "true")
                    Boolean asc
            );

    @Operation(
            summary = "Получение конкретного счета",
            description = "Получение счета по публичному ID"
    )
    @GetMapping("/{publicId}")
    AccountResponse getAccount(@PathVariable("publicId")
                               @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                               UUID publicId);

    @Operation(
            summary = "Закрытие счета",
            description = "Запрос на закрытие счета по публичному ID"
    )
    @PatchMapping("/{public}/close")
    ResponseEntity<Void> closeAccount(@PathVariable("publicId")
                                      @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                      UUID publicId);
}
