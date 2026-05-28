package ru.khan.bank.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khan.bank.operation.entity.MoneyOperation;

import java.util.Optional;

@Repository
public interface MoneyOperationRepository extends JpaRepository<MoneyOperation, Long> {
    Optional<MoneyOperation> findByIdempotencyKey(String idempotencyKey);
}
