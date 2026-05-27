package ru.khan.bank.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khan.bank.account.entity.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findAllByOwner(Long id, Pageable pageable);

    Optional<Account> findByPublicId(UUID publicId);
}
