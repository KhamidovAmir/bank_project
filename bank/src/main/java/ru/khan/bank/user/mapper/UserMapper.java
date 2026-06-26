package ru.khan.bank.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.user.dto.UserProfileResponse;
import ru.khan.bank.user.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserProfileResponse toUserProfileResponse(User user);

    UsersPageableResponse toUsersPageableResponse(User users);
}
