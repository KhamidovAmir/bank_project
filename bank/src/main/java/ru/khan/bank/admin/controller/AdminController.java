package ru.khan.bank.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.admin.dto.AccountsPageableResponse;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.admin.service.AdminService;
import ru.khan.bank.operation.entity.OperationsSort;
import ru.khan.bank.user.entity.UserSort;

import java.util.UUID;

@RestController
public class AdminController implements AdminApi{

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public Page<UsersPageableResponse> getUsers(Integer page, Integer size, UserSort sort, Boolean asc) {
        return adminService.getUsers(size, page, sort, asc);
    }

    public Page<AccountsPageableResponse> getAccounts(Integer page, Integer size, AccountSort sort, Boolean asc){
        return adminService.getAccounts(size, page, sort, asc);
    }

    public Page<OperationsPageableResponse> getOperations(Integer page, Integer size, OperationsSort sort, Boolean asc){
        return adminService.getOperations(size, page, sort, asc);
    }

    public ResponseEntity<Void> blockAccount(UUID accountPublicId){
        adminService.blockAccount(accountPublicId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> unblockAccount(UUID accountPublicId){
        adminService.unblockAccount(accountPublicId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> blockUser(UUID publicUserId){
        adminService.blockUser(publicUserId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> unblockUser(UUID publicUserId){
        adminService.unblockUser(publicUserId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> deleteUser(UUID publicUserId){
        adminService.deleteUser(publicUserId);
        return ResponseEntity.ok().build();
    }
}
