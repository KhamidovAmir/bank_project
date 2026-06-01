package ru.khan.bank.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.auth.dto.JwtUser;
import ru.khan.bank.auth.service.AuthUserProvider;
import ru.khan.bank.user.UserMapper;
import ru.khan.bank.user.dto.CreateUserCommand;
import ru.khan.bank.user.dto.UserProfileResponse;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.entity.UserStatus;
import ru.khan.bank.user.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthUserProvider userProvider;

    public UserService(UserRepository userRepository, UserMapper userMapper, AuthUserProvider userProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userProvider = userProvider;
    }

    @Transactional
    public User createUser(CreateUserCommand command) {

        if (userRepository.existsByEmail(command.email()))
            throw new RuntimeException("Email already exists, try another one");

        User user = new User(
                command.email(),
                command.passwordHash(),
                command.firstName(),
                command.lastName(),
                UserRole.CUSTOMER
                );

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }

    public User getCurrentUser(){
        JwtUser jwtUser = userProvider.getCurrentUser();
        return userRepository.findByEmail(jwtUser.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserProfileResponse getProfile(){
        User user = getCurrentUser();
        return userMapper.toUserProfileResponse(
                user.getEmail(),
                user.getPublicId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getStatus().name());
    }

    public boolean isActive(User user){
        return user.getStatus() == UserStatus.ACTIVE;
    }
    @Transactional(readOnly = true)
    public Page<UsersPageableResponse> getUsers(Pageable pageable){
        return userRepository.findAll(pageable)
                .map(user ->
                    userMapper.toUsersPageableResponse(
                            user.getPublicId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole(),
                            user.getStatus(),
                            user.getCreatedAt()
                            )
                );
    }

    public User getUserByPublicId(UUID publicUserId) {
        return userRepository.findByPublicId(publicUserId)
                .orElseThrow(() -> new RuntimeException("User with public id" + publicUserId + "not found"));
    }
}
