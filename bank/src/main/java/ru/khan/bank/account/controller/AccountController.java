package ru.khan.bank.account.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.account.dto.AccountPageResponse;
import ru.khan.bank.account.dto.AccountResponse;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.account.service.AccountService;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<CreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request){
        return ResponseEntity.status(201).body(accountService.createAccount(request));
    }

    @PostMapping("/my")
    public Page<AccountPageResponse> getMyAccounts(@RequestParam(value = "size", defaultValue = "20") Integer size,
                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "sort", defaultValue = "CREATED_AT") AccountSort sort,
                                                   @RequestParam(value = "asc", defaultValue = "true") Boolean asc) {
        return accountService.getMyAccounts(size, page, sort, asc);
    }

    @GetMapping("/{publicId}")
    public AccountResponse getAccount(@PathVariable("publicId") UUID publicId) {
        return accountService.getAccountByPublicId(publicId);
    }
    @PatchMapping("/{public}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable("public") UUID publicId) {
        accountService.closeAccount(publicId);
        return ResponseEntity.ok().build();
    }
}
