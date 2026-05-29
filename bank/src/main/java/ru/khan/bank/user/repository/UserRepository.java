package ru.khan.bank.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khan.bank.admin.dto.UsersPageableResponse;
import ru.khan.bank.user.entity.User;

import java.lang.ScopedValue;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    Page<User> findAll(Pageable pageable);

    Optional<User> findByPublicId(UUID publicUserId);
}
