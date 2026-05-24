package ru.khan.bank.user;

import org.springframework.stereotype.Component;
import ru.khan.bank.user.dto.UserProfileResponse;

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

}
