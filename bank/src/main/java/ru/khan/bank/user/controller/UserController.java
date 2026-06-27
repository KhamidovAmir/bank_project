package ru.khan.bank.user.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.khan.bank.user.dto.UserProfileResponse;
import ru.khan.bank.user.service.UserService;

@RestController
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserProfileResponse getMyProfile(){
        return userService.getProfile();
    }

}
