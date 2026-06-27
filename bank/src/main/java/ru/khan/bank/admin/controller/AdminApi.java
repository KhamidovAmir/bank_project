package ru.khan.bank.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.admin.dto.AccountsPageableResponse;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.user.entity.UserSort;

import java.util.UUID;

@RequestMapping("/admin")
@Tag(name = "Контроллер работы с пользователями", description = "Контроллер контура работы с пользователями")
public interface AdminApi {

    @GetMapping("/users")
    @Operation(summary = "Получение пользователей с помощью пагинации")
    Page<UsersPageableResponse> getUsers(
            @RequestParam(value = "size", defaultValue = "20")
            @Parameter(description = "Размер страницы - пагинации", example = "20", required = true)
            Integer size,

            @RequestParam(value = "page", defaultValue = "0")
            @Parameter(description = "Номер страницы", example = "0", required = true)
            Integer page,

            @RequestParam(value = "sort", defaultValue = "CREATED_AT")
            @Parameter(description = "Сортировка по полю", example = "CREATED_AT", required = true)
            UserSort sort,

            @RequestParam(value = "asc", defaultValue = "true")
            @Parameter(description = "Сортировка по направлению: true — по возрастанию, false — по убыванию", example = "true", required = true)
            Boolean asc);

    @GetMapping("/accounts")
    @Operation(summary = "Получение счетов с помощью пагинации")
    Page<AccountsPageableResponse> getAccounts(
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
            @Parameter(description = "Сортировка по направлению: true — по возрастанию, false — по убыванию", example = "true", required = true)
            Boolean asc);

    @GetMapping
    @Operation(summary = "Получение операций с помощью пагинации")
    Page<OperationsPageableResponse> getOperations(
            @RequestParam(value = "size", defaultValue = "20")
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

    @PatchMapping("/accounts/{accountPublicId}/block")
    @Operation(summary = "Блокировка счета по публичному ID формата UUID")
    ResponseEntity<Void> blockAccount(@PathVariable
                                      @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                      UUID accountPublicId);

    @PatchMapping("/accounts/{accountPublicId}/unblock")
    @Operation(summary = "Разблокировка счета по публичному ID формата UUID")
    ResponseEntity<Void> unblockAccount(@PathVariable
                                        @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                        UUID accountPublicId);

    @PatchMapping("/users/{userId}/block")
    @Operation(summary = "Блокировка пользователя по публичному ID формата UUID")
    ResponseEntity<Void> blockUser(@PathVariable
                                   @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                   UUID userId);

    @PatchMapping("/users/{userId}/unblock")
    @Operation(summary = "Разблокировка счета по публичному ID формата UUID")
    ResponseEntity<Void> unblockUser(@PathVariable
                                     @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                     UUID userId);

    @PatchMapping("/users/{userId}/delete")
    @Operation(summary = "Удаление пользователя по публичному ID формата UUID")
    ResponseEntity<Void> deleteUser(@PathVariable
                                    @Parameter(description = "Публичный ID формата UUID", example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92")
                                    UUID userId);
}
