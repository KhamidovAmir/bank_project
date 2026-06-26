package ru.khan.bank.account.controller;

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
public class AccountController implements  AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest request){
        return ResponseEntity.status(201).body(accountService.createAccount(request));
    }

    public Page<AccountPageResponse> getMyAccounts(Integer size, Integer page, AccountSort sort, Boolean asc) {
        return accountService.getMyAccounts(size, page, sort, asc);
    }

    public AccountResponse getAccount(UUID publicId) {
        return accountService.getAccountByPublicId(publicId);
    }

    public ResponseEntity<Void> closeAccount(UUID publicId) {
        accountService.closeAccount(publicId);
        return ResponseEntity.ok().build();
    }
}
