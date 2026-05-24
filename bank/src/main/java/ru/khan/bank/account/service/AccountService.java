package ru.khan.bank.account.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.mapper.AccountMapper;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, UserService userService, AccountNumberGenerator accountNumberGenerator, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.accountNumberGenerator = accountNumberGenerator;
        this.accountMapper = accountMapper;
    }

    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest request){
        User user = userService.getCurrentUser();

        var accountNumber = accountNumberGenerator.generate();

        Account account = new Account(user, accountNumber, request.currency());

        var saved = accountRepository.save(account);
        return accountMapper.toCreateAccountResponse(saved.getPublicId(), saved.getAccountNumber(), saved.getBalance(), saved.getStatus());

    }
}
