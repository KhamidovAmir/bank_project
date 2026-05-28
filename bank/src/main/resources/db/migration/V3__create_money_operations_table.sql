CREATE TABLE money_operations
(
    id BIGSERIAL PRIMARY KEY,

    public_id UUID NOT NULL UNIQUE,
    operation_number VARCHAR(255) NOT NULL UNIQUE,

    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,

    from_account_id BIGINT,
    to_account_id BIGINT,

    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    description VARCHAR(500),
    failure_reason VARCHAR(500),

    idempotency_key VARCHAR(255) UNIQUE,

    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,

    CONSTRAINT chk_money_operations_type
        CHECK (type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),

    CONSTRAINT chk_money_operations_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),

    CONSTRAINT chk_money_operations_amount_positive
        CHECK (amount > 0),

    CONSTRAINT chk_money_operations_accounts_by_type
        CHECK (
            (type = 'DEPOSIT' AND from_account_id IS NULL AND to_account_id IS NOT NULL)
                OR
            (type = 'WITHDRAW' AND from_account_id IS NOT NULL AND to_account_id IS NULL)
                OR
            (type = 'TRANSFER' AND from_account_id IS NOT NULL AND to_account_id IS NOT NULL AND from_account_id <> to_account_id)
            )
);