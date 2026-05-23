package ru.khan.bank.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khan.bank.auth.dto.CreateUserRequest;
import ru.khan.bank.user.dto.CreateUserCommand;
import ru.khan.bank.user.entity.User;
import ru.khan.bank.user.entity.UserRole;
import ru.khan.bank.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

}
