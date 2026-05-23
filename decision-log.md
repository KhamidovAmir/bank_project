# Decision Log

## ADR-001: Modular monolith instead of microservices

### Status

Accepted

### Context

Проект является учебным backend-проектом мини-банковской системы.

Микросервисная архитектура на старте добавила бы лишнюю сложность:

- network communication
- service discovery
- distributed transactions
- separate deployments
- harder debugging
- harder testing
- infrastructure overhead

### Decision

На старте проект реализуется как modular monolith.

### Consequences

Плюсы:

- проще разработка
- проще тестирование
- проще деплой
- проще отладка
- быстрее MVP

Минусы:

- все модули пока деплоятся вместе
- нет независимого масштабирования
- границы модулей нужно соблюдать дисциплиной в коде

---

## ADR-002: Use publicId for external API

### Status

Accepted

### Context

Внутренний database id не должен использоваться как публичный идентификатор в API.

### Decision

Для внешнего API использовать `publicId`.

Внутренний `id` оставить для базы данных и связей между сущностями.

### Consequences

Плюсы:

- API не раскрывает sequence ids
- сложнее угадывать чужие ресурсы
- легче менять внутреннюю структуру БД

Минусы:

- появляется дополнительное поле
- нужны unique constraints
- чуть больше кода

---

## ADR-003: BigDecimal for money

### Status

Accepted

### Context

Денежные значения нельзя хранить через double или float из-за проблем с точностью.

### Decision

В MVP деньги хранятся через BigDecimal.

### Consequences

Плюсы:

- точное хранение decimal-значений
- подходит для денежных операций

Минусы:

- нужно внимательно работать со scale
- сравнение делать через compareTo
- нужно контролировать rounding

---

## ADR-004: Optimistic locking for Account

### Status

Accepted

### Context

Баланс счета может изменяться конкурентно несколькими запросами.

Например:

- два снятия одновременно
- перевод и снятие одновременно
- несколько переводов одновременно

### Decision

В Account используется поле `version` и optimistic locking.

### Consequences

Плюсы:

- нет постоянной блокировки строки
- хорошо подходит для изучения concurrency
- конфликт изменений можно явно обработать

Минусы:

- при конфликте операция может упасть
- нужно продумать retry logic
- под высокой конкуренцией может быть много конфликтов

---

## ADR-005: Idempotency key for money operations

### Status

Accepted

### Context

Клиент может повторно отправить один и тот же запрос.

Например:

- из-за timeout
- из-за retry
- из-за двойного клика
- из-за сетевой ошибки

Без защиты одна операция может выполниться несколько раз.

### Decision

Для денежных операций добавить `idempotencyKey`.

### Consequences

Плюсы:

- повторный запрос не создает новую денежную операцию
- система безопаснее при retry
- можно тестировать реальные проблемы платежных систем

Минусы:

- нужно хранить ключи
- нужно продумать unique constraint
- нужно решить срок жизни ключей в будущем