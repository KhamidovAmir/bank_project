package ru.khan.bank.account.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.dto.AccountPageResponse;
import ru.khan.bank.account.dto.AccountResponse;
import ru.khan.bank.account.dto.CreateAccountRequest;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.account.mapper.AccountMapper;
import ru.khan.bank.account.repository.AccountRepository;
import ru.khan.bank.admin.dto.AccountsPageableResponse;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.service.UserService;

import java.util.List;
import java.util.UUID;

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

        return accountRepository.findAllByOwnerId(user.getId(), pageable)
                .map(a -> accountMapper.toAccountPageResponse(
                        a.getAccountNumber(),
                        a.getBalance(),
                        a.getCurrency(),
                        a.getStatus()
                        ));
    }

    public AccountResponse getAccountByPublicId(UUID publicId) {
        User user = userService.getCurrentUser();

        Account account = accountRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getOwner().getId().equals(user.getId()))
            throw new RuntimeException("You don't have access to this perform");

        return accountMapper.toAccountResponse(
                account.getPublicId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus()
        );
    }

    public void closeAccount(UUID publicId) {
        User user = userService.getCurrentUser();
        var account = accountRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!account.getOwner().getId().equals(user.getId()))
            throw new RuntimeException("You don't have access to this perform");
        account.close();
    }
    public Account getAccount(UUID publicId) {
        return accountRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional(readOnly = true)
    public Page<AccountsPageableResponse> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(account -> accountMapper.toAccountsPageableResponse(
                        account.getPublicId(),
                        account.getAccountNumber(),
                        account.getOwner().getPublicId(),
                        account.getOwner().getFirstName(),
                        account.getOwner().getLastName(),
                        account.getStatus()
                        )
                );
    }

    public List<Long> getAllMyAccounts(Long ownerId) {
        return accountRepository.findIdsByOwnerId(ownerId);
    }
}
