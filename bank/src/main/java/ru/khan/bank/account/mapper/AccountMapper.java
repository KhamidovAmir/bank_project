package ru.khan.bank.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.khan.bank.account.dto.AccountPageResponse;
import ru.khan.bank.account.dto.AccountResponse;
import ru.khan.bank.account.dto.CreateAccountResponse;
import ru.khan.bank.account.entity.Account;
import ru.khan.bank.admin.dto.AccountsPageableResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    CreateAccountResponse toCreateAccountResponse(Account account);

    AccountPageResponse toAccountPageResponse(Account account);

    AccountResponse toAccountResponse(Account account);

    AccountsPageableResponse toAccountsPageableResponse(Account account);
}
