package ru.khan.bank.account.mapper;

import org.springframework.stereotype.Component;
import ru.khan.bank.account.dto.AccountPageResponse;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.AccountStatus;
import ru.khan.bank.account.entity.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountMapper {

    public CreateAccountResponse toCreateAccountResponse(UUID publicId, String accountNumber, BigDecimal balance, AccountStatus status) {
        return  new CreateAccountResponse(publicId, accountNumber, balance, status);
    }
    public AccountPageResponse toAccountPageResponse(String accountNumber, BigDecimal balance, Currency currency, AccountStatus status) {
        return  new AccountPageResponse(accountNumber, balance, currency, status);
    }
}
