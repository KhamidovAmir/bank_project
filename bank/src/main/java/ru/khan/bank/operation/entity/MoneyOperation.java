package ru.khan.bank.operation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.khan.bank.account.entity.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "money_operations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoneyOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(name = "operation_number", nullable = false, unique = true, updatable = false)
    private String operationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationStatus status;

    @Column(name = "from_account_id")
    private Long fromAccountId;

    @Column(name = "to_account_id")
    private Long toAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(length = 500)
    private String description;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "idempotency_key", unique = true, updatable = false)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private MoneyOperation(
            String operationNumber,
            OperationType type,
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount,
            Currency currency,
            String description,
            String idempotencyKey
    ) {
        this.publicId = UUID.randomUUID();
        this.operationNumber = operationNumber;
        this.type = requireType(type);
        this.status = OperationStatus.PENDING;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = requirePositiveAmount(amount);
        this.currency = requireCurrency(currency);
        this.description = normalize(description);
        this.idempotencyKey = normalize(idempotencyKey);
        this.createdAt = LocalDateTime.now();

        validateAccounts();
    }

    public static MoneyOperation deposit(
            String operationNumber,
            Long toAccountId,
            BigDecimal amount,
            Currency currency,
            String description,
            String idempotencyKey
    ) {
        return new MoneyOperation(
                operationNumber,
                OperationType.DEPOSIT,
                null,
                requireAccountId(toAccountId, "toAccountId is required for deposit"),
                amount,
                currency,
                description,
                idempotencyKey
        );
    }

    public static MoneyOperation withdraw(
            String operationNumber,
            Long fromAccountId,
            BigDecimal amount,
            Currency currency,
            String description,
            String idempotencyKey
    ) {
        return new MoneyOperation(
                operationNumber,
                OperationType.WITHDRAW,
                requireAccountId(fromAccountId, "fromAccountId is required for withdraw"),
                null,
                amount,
                currency,
                description,
                idempotencyKey
        );
    }

    public static MoneyOperation transfer(
            String operationNumber,
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount,
            Currency currency,
            String description,
            String idempotencyKey
    ) {
        return new MoneyOperation(
                operationNumber,
                OperationType.TRANSFER,
                requireAccountId(fromAccountId, "fromAccountId is required for transfer"),
                requireAccountId(toAccountId, "toAccountId is required for transfer"),
                amount,
                currency,
                description,
                idempotencyKey
        );
    }

    public void complete() {
        ensurePending();

        this.status = OperationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.failureReason = null;
    }

    public void fail(String reason) {
        ensurePending();

        String normalizedReason = normalize(reason);

        if (normalizedReason == null) {
            throw new IllegalArgumentException("Failure reason is required");
        }

        this.status = OperationStatus.FAILED;
        this.failureReason = normalizedReason;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        ensurePending();

        this.status = OperationStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return status == OperationStatus.PENDING;
    }

    public boolean isCompleted() {
        return status == OperationStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == OperationStatus.FAILED;
    }

    public boolean isCancelled() {
        return status == OperationStatus.CANCELLED;
    }

    private void ensurePending() {
        if (status != OperationStatus.PENDING) {
            throw new IllegalStateException("Only pending operation can be changed");
        }
    }

    private void validateAccounts() {
        if (type == OperationType.DEPOSIT) {
            if (fromAccountId != null) {
                throw new IllegalArgumentException("Deposit must not have fromAccountId");
            }

            if (toAccountId == null) {
                throw new IllegalArgumentException("Deposit requires toAccountId");
            }
        }

        if (type == OperationType.WITHDRAW) {
            if (fromAccountId == null) {
                throw new IllegalArgumentException("Withdraw requires fromAccountId");
            }

            if (toAccountId != null) {
                throw new IllegalArgumentException("Withdraw must not have toAccountId");
            }
        }

        if (type == OperationType.TRANSFER) {
            if (fromAccountId == null || toAccountId == null) {
                throw new IllegalArgumentException("Transfer requires both accounts");
            }

            if (fromAccountId.equals(toAccountId)) {
                throw new IllegalArgumentException("Transfer between same account is not allowed");
            }
        }
    }

    private static OperationType requireType(OperationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Operation type is required");
        }

        return type;
    }

    private static Currency requireCurrency(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency is required");
        }

        return currency;
    }

    private static BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        return amount;
    }

    private static Long requireAccountId(Long accountId, String message) {
        if (accountId == null) {
            throw new IllegalArgumentException(message);
        }

        return accountId;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}