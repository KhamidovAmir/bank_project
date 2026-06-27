package ru.khan.bank.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.khan.bank.user.dto.UserProfileResponse;

@RequestMapping("/users")
@Tag(name = "Контроллер работы с пользователем", description = "Контур для работы с личными данными и прочими данными пользователя")
public interface UserApi {

    @GetMapping("/me")
    @Operation(summary = "Информация по профилю пользователя")
    UserProfileResponse getMyProfile();
}
