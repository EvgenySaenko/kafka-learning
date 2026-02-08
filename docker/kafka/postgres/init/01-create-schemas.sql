-- Создаём схемы заранее, чтобы Liquibase мог положить свои служебные таблицы
-- (databasechangelog / databasechangeloglock) прямо в нужную схему с первого запуска.

CREATE SCHEMA IF NOT EXISTS outbox_order;
CREATE SCHEMA IF NOT EXISTS outbox_billing;

-- Даём права пользователю приложения (чтобы Liquibase мог создавать таблицы)
GRANT USAGE, CREATE ON SCHEMA outbox_order TO outbox;
GRANT USAGE, CREATE ON SCHEMA outbox_billing TO outbox;
