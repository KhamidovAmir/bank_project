package ru.khan.bank.admin.dto;

import ru.khan.bank.account.entity.AccountStatus;

import java.util.UUID;

public record AccountsPageableResponse
        (
                UUID accountPublicId,
                String accountNumber,
                UUID ownerPublicId,
                String ownerFirstName,
                String ownerLastName,
                AccountStatus status
        ) {
}
