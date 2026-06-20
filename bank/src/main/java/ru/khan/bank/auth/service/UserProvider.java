package ru.khan.bank.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.khan.bank.auth.dto.JwtUser;
import ru.khan.bank.common.exception.exceptions.AuthenticationException;

@Component
public class UserProvider implements AuthUserProvider {
    @Override
    public JwtUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||  !authentication.isAuthenticated())
            throw new AuthenticationException("User is not authenticated");
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof JwtUser jwtUser))
            throw new AuthenticationException("Invalid authentication principal");
        return jwtUser;
    }
}
