package ru.khan.bank.auth.service;

import ru.khan.bank.auth.dto.JwtUser;

public interface AuthUserProvider {
    public JwtUser getCurrentUser();
}
