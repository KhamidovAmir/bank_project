CREATE TABLE accounts(

    id BIGSERIAL PRIMARY KEY,

    public_id UUID NOT NULL UNIQUE,

    account_number VARCHAR(64) NOT NULL UNIQUE,

    owner_id BIGINT NOT NULL,

    balance NUMERIC(19,2) NOT NULL,

    currency VARCHAR(3) NOT NULL
                     CHECK ( currency IN ('RUB') ),

    status VARCHAR(20) NOT NULL
                     CHECK ( status IN ('ACTIVE', 'BLOCKED', 'CLOSED') ),

    version INTEGER NOT NULL ,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_accounts_owner
                     FOREIGN KEY (owner_id)
                     REFERENCES users (id),

    CONSTRAINT chk_accounts_balance_non_negative
                     CHECK ( balance >= 0 )
)