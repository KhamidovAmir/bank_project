CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    public_id UUID NOT NULL UNIQUE,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash TEXT NOT NULL,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100) NOT NULL,

    role VARCHAR(50) NOT NULL
        CHECK ( role IN ('CUSTOMER', 'ADMIN') ),

    status VARCHAR(50) NOT NULL
        CHECK ( status IN ('ACTIVE', 'BLOCKED' , 'DELETED') ),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
)