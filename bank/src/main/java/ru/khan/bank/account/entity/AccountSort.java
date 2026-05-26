package ru.khan.bank.account.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum AccountSort {
    CREATED_AT("createdAt"),
    BALANCE( "balance"),
    STATUS("status");

    private final String field;

    public Sort toSort(boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

}
