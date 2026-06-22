package ru.khan.bank.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.admin.dto.AccountsPageableResponse;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.common.exception.exceptions.AccessDeniedException;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.operation.service.MoneyOperationService;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserSort;
import ru.khan.bank.user.service.UserService;

import java.util.UUID;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private final UserService userService;
    private final AccountService accountService;
    private final MoneyOperationService moneyOperationService;

    public AdminService(UserService userService, AccountService accountService, MoneyOperationService moneyOperationService) {
        this.userService = userService;
        this.accountService = accountService;
        this.moneyOperationService = moneyOperationService;
    }

    public Page<UsersPageableResponse> getUsers(Integer size, Integer page, UserSort sort, Boolean asc) {

        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return userService.getUsers(pageable);
    }

    public Page<AccountsPageableResponse> getAccounts(Integer size, Integer page, AccountSort sort, Boolean asc) {

        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return accountService.getAccounts(pageable);
    }

    @Transactional
    public void unblockAccount(UUID accountPublicId) {
        Account account = accountService.getAccount(accountPublicId);
        account.activate();

    }

    @Transactional
    public void blockAccount(UUID accountPublicId) {
        Account account = accountService.getAccount(accountPublicId);
        account.block();
    }

    @Transactional
    public void blockUser(UUID publicUserId) {
        User target = userService.getUserByPublicId(publicUserId);
        target.block();
    }

    @Transactional
    public void unblockUser(UUID publicUserId) {
        User target = userService.getUserByPublicId(publicUserId);
        target.activate();
    }

    public void deleteUser(UUID publicUserId) {
        User target = userService.getUserByPublicId(publicUserId);
        target.delete();
    }

    public Page<OperationsPageableResponse> getOperations(Integer size, Integer page, OperationsSort sort, Boolean asc) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));
        return moneyOperationService.getOperations(pageable);
    }

}
