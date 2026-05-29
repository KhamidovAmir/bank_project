package ru.khan.bank.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.account.service.AccountService;
import ru.khan.bank.admin.dto.AccountsPageableResponse;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserSort;
import ru.khan.bank.user.service.UserService;

@Service
public class AdminService {

    private final UserService userService;
    private final AccountService accountService;

    public AdminService(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public Page<UsersPageableResponse> getUsers(Integer size, Integer page, UserSort sort, Boolean asc) {

        User user = userService.getCurrentUser();

        isAdmin(user);

        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return userService.getUsers(pageable);
    }

    private void isAdmin(User user) {
        if (user.getRole() != UserRole.ADMIN)
            throw new RuntimeException("You are not an admin");
    }

    public Page<AccountsPageableResponse> getAccounts(Integer size, Integer page, AccountSort sort, Boolean asc) {
        User user = userService.getCurrentUser();
        isAdmin(user);

        Pageable pageable = PageRequest.of(page, size, sort.toSort(asc));

        return accountService.getAccounts(pageable);
    }
}
