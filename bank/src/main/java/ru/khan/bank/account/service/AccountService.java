package ru.khan.bank.account.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.dto.AccountPageResponse;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.AccountSort;
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

    public Page<AccountPageResponse> getMyAccounts(Integer size, Integer page, AccountSort sort, Boolean asc) {

        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return accountRepository.findAllByOwner(user.getId(), pageable)
                .map(a -> new AccountPageResponse(
                        a.getAccountNumber(),
                        a.getBalance(),
                        a.getCurrency(),
                        a.getStatus()
                        ));
    }
}
