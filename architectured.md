# Architecture

## Подход

Проект разрабатывается как modular monolith.

Это означает, что приложение деплоится как один backend-сервис, но внутри разделено на независимые логические модули.

Цель: сохранить простоту монолита, но заранее подготовить код к возможному будущему разделению на микросервисы.

## Основные модули

```text
auth
user
account
operation
admin
common
```

### Ответственность модулей

#### auth
Отвечает за:

- регистрацию
- логин
- JWT
- аутентификацию
- работу с паролями 

#### user
Отвечает за:

- пользователей
- роли
- статусы пользователей
- профиль пользователя

#### account
Отвечает за:

- банковские счета
- баланс
- статус счета
- accountNumber
- блокировку и разблокировку счета
- optimistic locking

#### operation
Отвечает за:

- пополнение
- снятие
- переводы
- историю операций
- статусы операций
- idempotency

#### admin
Отвечает за:

- административные endpoints
- просмотр пользователей
- просмотр счетов
- просмотр операций
- блокировку счетов

#### common
Отвечает за общие вещи:

- exceptions
- base response/error response
- utils
- constants
- common validation
- time provider, если понадобится

### Правила зависимостей

Модули не должны хаотично обращаться друг к другу.

Нежелательно:
```
operation -> AccountRepository
auth -> AccountRepository
admin -> любые repository напрямую без необходимости
```
Желательно:
```
operation -> AccountService / AccountFacade
admin -> UserService / AccountService / OperationService
```
Repository должны использоваться внутри своего модуля.

#### Транзакционные границы

Денежные операции должны выполняться внутри database transaction.

Для атомарности операций и целостности данных о деньгах.

#### Locking strategy

В MVP используется optimistic locking через поле:

Account.version

Цель:

- защититься от конкурентного изменения баланса
- получить опыт работы с @Version
- проверить поведение под нагрузкой

#### Idempotency

Для денежных операций поддерживается idempotencyKey.

Цель:

- защититься от повторной отправки одного и того же запроса
- не создавать дубликаты операций при retry
- протестировать поведение под нагрузкой

#### Public ID

Для внешнего API используются public identifiers.

Внутренний id остается техническим database id.

Пример:

id       -> Long, internal
publicId -> UUID, external

Это снижает риск раскрытия внутренней структуры базы данных.

#### Деньги

Деньги нельзя хранить через:

double
float

В MVP используется:

BigDecimal

Для BigDecimal обязательно контролировать:

- scale
- rounding
- сравнение через compareTo, а не equals

#### Расширение проекта

Потенциальные будущие модули:

card
notification
audit
fraud
payment
report

#### Возможный переход к микросервисам

Потенциальные кандидаты на вынос:

auth-service
account-service
operation-service
notification-service
audit-service

Но переход к микросервисам не входит в MVP.

Сначала система должна быть реализована как работающий modular monolith.
