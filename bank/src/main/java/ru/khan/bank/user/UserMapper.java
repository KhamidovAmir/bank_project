package ru.khan.bank.user;

import org.springframework.stereotype.Component;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.user.dto.UserProfileResponse;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class UserMapper {

    public UserProfileResponse toUserProfileResponse(String email, UUID publicId, String firstName, String lastName, String role, String status) {
        return new UserProfileResponse
                (
                        email,
                        publicId,
                        firstName,
                        lastName,
                        role,
                        status
                );
    }
    public UsersPageableResponse toUsersPageableResponse(UUID publicId, String firstName, String lastName, UserRole role, UserStatus status, LocalDateTime createdAt) {
        return new UsersPageableResponse(
                publicId,
                firstName,
                lastName,
                role,
                status,
                createdAt
        );
    }

}
