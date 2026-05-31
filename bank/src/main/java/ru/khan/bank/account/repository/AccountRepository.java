package ru.khan.bank.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.khan.bank.account.entity.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findAllByOwnerId(Long id, Pageable pageable);

    Optional<Account> findByPublicId(UUID publicId);

    @Query("""
    SELECT a.id
        FROM Account a
            WHERE a.owner.id = :ownerId
    """)
    List<Long> findIdsByOwnerId(Long ownerId);

    Optional<Account> findByOwnerId(Long ownerId);
}
