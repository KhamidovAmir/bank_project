package ru.khan.bank.operation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum OperationsSort {

    OPERATIONS_TYPE("type"),
    OPERATIONS_STATUS("status"),
    OPERATIONS_CREATED_AT("createdAt"),
    OPERATIONS_COMPLETED_AT("completedAt");

    private final String field;

    public Sort toSort(boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}
