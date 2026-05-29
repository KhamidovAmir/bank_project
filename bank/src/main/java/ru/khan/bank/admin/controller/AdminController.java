package ru.khan.bank.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khan.bank.account.entity.AccountSort;
import ru.khan.bank.admin.dto.AccountsPageableResponse;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.admin.service.AdminService;
import ru.khan.bank.user.entity.UserSort;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public Page<UsersPageableResponse> getUsers(@RequestParam(value = "size", defaultValue = "20") Integer size,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "sort", defaultValue = "CREATED_AT") UserSort sort,
                                                @RequestParam(value = "asc", defaultValue = "true") Boolean asc){
        return adminService.getUsers(size, page, sort, asc);
    }
    @GetMapping("/accounts")
    public Page<AccountsPageableResponse> getAccounts(@RequestParam(value = "size", defaultValue = "20") Integer size,
                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "sort", defaultValue = "CREATED_AT") AccountSort sort,
                                                      @RequestParam(value = "asc", defaultValue = "true") Boolean asc){
        return adminService.getAccounts(size, page, sort, asc);
    }
    @PatchMapping("/accounts/{accountPublicId}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable UUID accountPublicId){
        adminService.blockAccount(accountPublicId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/accounts/{accountPublicId}/unblock")
    public ResponseEntity<Void> unblockAccount(@PathVariable UUID accountPublicId){
        adminService.unblockAccount(accountPublicId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable UUID publicUserId){
        adminService.blockUser(publicUserId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/users/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable UUID publicUserId){
        adminService.unblockUser(publicUserId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/users/{userId}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID publicUserId){
        adminService.deleteUser(publicUserId);
        return ResponseEntity.ok().build();
    }
}
