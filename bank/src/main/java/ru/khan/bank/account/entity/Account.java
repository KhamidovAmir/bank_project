package ru.khan.bank.account.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.khan.bank.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false, unique = true, updatable = false)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Account(User owner, String accountNumber, Currency currency) {
        if (owner == null) {
            throw new RuntimeException("Owner is required");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new RuntimeException("Account number is required");
        }
        if (currency == null) {
            throw new RuntimeException("Currency is required");
        }

        this.publicId = UUID.randomUUID();
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    public void deposit(BigDecimal amount) {
        validateActive();
        validatePositiveAmount(amount);

        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateActive();
        validatePositiveAmount(amount);

        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        this.balance = this.balance.subtract(amount);
    }

    public void block() {
        changeStatus(AccountStatus.BLOCKED);
    }

    public void activate() {
        changeStatus(AccountStatus.ACTIVE);
    }

    public void close() {
        if (this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account with non-zero balance cannot be closed");
        }

        changeStatus(AccountStatus.CLOSED);
    }

    private void changeStatus(AccountStatus newStatus) {
        validateStatusTransition(newStatus);

        this.status = newStatus;
    }

    private void validateStatusTransition(AccountStatus newStatus) {
        boolean validTransition =
                (this.status == AccountStatus.ACTIVE && newStatus == AccountStatus.BLOCKED) ||
                        (this.status == AccountStatus.BLOCKED && newStatus == AccountStatus.ACTIVE) ||
                        (this.status == AccountStatus.ACTIVE && newStatus == AccountStatus.CLOSED) ||
                        (this.status == AccountStatus.BLOCKED && newStatus == AccountStatus.CLOSED);

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid account status transition: " + this.status + " -> " + newStatus
            );
        }
    }

    private void validateActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Operation is allowed only for active account");
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.version == null) {
            this.version = 0;
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
