# Bank MVP

## Описание

MVP — первая рабочая версия мини-банковской системы.

Цель MVP: реализовать базовый функционал управления пользователями, счетами, денежными операциями и историей операций.

## Функционал

### Auth

- регистрация
- логин
- JWT
- роли

### User

- просмотр своего профиля

### Account

- создание банковского счета
- просмотр своих счетов
- просмотр конкретного счета
- просмотр баланса
- закрытие счета
- блокировка счета админом
- разблокировка счета админом

### Operation

- пополнение счета
- снятие денег
- перевод между своими и чужими счетами
- история операций

## Не входит в MVP

- карты
- кредиты
- проценты
- комиссии
- мультивалюта
- валютный обмен
- микросервисы
- Kafka/RabbitMQ
- внешние платежные системы
- уведомления
- админ-панель как frontend

## Роли

### CUSTOMER

Может:

- зарегистрироваться
- войти в систему
- просматривать свой профиль
- создавать свои счета
- просматривать свои счета
- пополнять свои счета
- снимать деньги со своих счетов
- переводить деньги между счетами
- просматривать свою историю операций

### ADMIN

Может:

- просматривать всех пользователей
- просматривать все счета
- блокировать счета
- разблокировать счета
- просматривать все операции

## Сущности

### User

- id — Long, unique, internal database id
- publicId — UUID, unique, immutable, external id
- email — String, unique
- passwordHash — String
- firstName — String
- lastName — String
- role — Enum
- status — Enum
- createdAt — LocalDateTime
- updatedAt — LocalDateTime

### UserRole

- CUSTOMER
- ADMIN

### UserStatus

- ACTIVE
- BLOCKED
- DELETED

---

### Account

- id — Long, unique, internal database id
- publicId — UUID, unique, immutable, external id
- accountNumber — String, unique, business account number
- owner — User, ManyToOne, immutable
- balance — BigDecimal
- currency — Enum
- status — Enum
- version — Integer, optimistic locking
- createdAt — LocalDateTime
- updatedAt — LocalDateTime

### Currency

- RUB

На MVP используется только RUB.

### AccountStatus

- ACTIVE
- BLOCKED
- CLOSED

---

### MoneyOperation

- id — Long, unique, internal database id
- publicId — UUID, unique, immutable, external id
- operationNumber — String, unique, business operation number
- type — Enum
- status — Enum
- fromAccountId — Long, nullable
- toAccountId — Long, nullable
- amount — BigDecimal
- currency — Enum
- description — String
- failureReason — String, nullable
- idempotencyKey — String, nullable
- createdAt — LocalDateTime
- completedAt — LocalDateTime, nullable

### OperationType

- DEPOSIT
- WITHDRAW
- TRANSFER

### OperationStatus

- PENDING
- COMPLETED
- FAILED
- CANCELLED

## Бизнес-ограничения

1. Нельзя создать счет для заблокированного пользователя.

2. Нельзя проводить операции по заблокированному пользователю.

3. Нельзя проводить операции по заблокированному счету.

4. Нельзя проводить операции по закрытому счету.

5. Нельзя снять больше, чем есть на балансе.

6. Нельзя переводить сумму меньше или равную нулю.

7. Нельзя переводить с закрытого или заблокированного счета.

8. Нельзя переводить на закрытый или заблокированный счет.

9. Нельзя переводить деньги самому себе на тот же самый счет.

10. Пополнение доступно только на свой счет.

11. Снятие доступно только со своего счета.

12. История операций видна владельцу счета и админу.

13. Перевод должен быть атомарным: либо списание и зачисление выполняются вместе, либо не выполняется ничего.

14. Деньги нельзя хранить через double или float.

15. Все денежные операции должны сохраняться в истории операций.

16. Повторный запрос с тем же idempotencyKey не должен создавать повторную операцию.

## State transitions

### UserStatus

Разрешено:
- ACTIVE -> BLOCKED
- BLOCKED -> DELETED
- ACTIVE -> DELETED
- BLOCKED -> ACTIVE

Запрещено:
- DELETED -> ACTIVE
- DELETED -> BLOCKED

### AccountStatus

Разрешено:

- ACTIVE -> BLOCKED
- BLOCKED -> ACTIVE
- ACTIVE -> CLOSED
- BLOCKED -> CLOSED

Запрещено:

- CLOSED -> ACTIVE
- CLOSED -> BLOCKED

### OperationStatus

Разрешено:

- PENDING -> COMPLETED
- PENDING -> FAILED
- PENDING -> CANCELLED

Запрещено:

- COMPLETED -> FAILED
- COMPLETED -> CANCELLED
- FAILED -> COMPLETED
- CANCELLED -> COMPLETED

## API MVP

### Auth

- POST /api/auth/register
- POST /api/auth/login

### User

- GET /api/users/me

### Customer accounts

- POST /api/accounts
- GET /api/accounts
- GET /api/accounts/{accountId}
- GET /api/accounts/{accountId}/balance

### Operations

- POST /api/accounts/{accountId}/deposit
- POST /api/accounts/{accountId}/withdraw
- POST /api/transfers
- GET /api/operations
- GET /api/accounts/{accountId}/operations

### Admin

- GET /api/admin/users
- GET /api/admin/accounts
- PATCH /api/admin/accounts/{accountId}/block
- PATCH /api/admin/accounts/{accountId}/unblock
- GET /api/admin/operations