package ru.khan.bank.account.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SimpleAccountNumberGenerator implements AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generate() {
        return "40817" + randomDigits(15);
    }

    private String randomDigits(int length) {
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append(RANDOM.nextInt(10));
        }

        return result.toString();
    }
}