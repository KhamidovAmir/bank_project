package ru.khan.bank.operation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khan.bank.operation.entity.MoneyOperation;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoneyOperationRepository extends JpaRepository<MoneyOperation, Long> {
    Optional<MoneyOperation> findByIdempotencyKey(String idempotencyKey);

    Page<MoneyOperation> findAllByFromAccountIdOrToAccountId(Long id, Long id1, Pageable pageable);

    Page<MoneyOperation> findAllByFromAccountIdInOrToAccountIdIn(List<Long> accountsId, List<Long> accountsId1, Pageable pageable);
}
