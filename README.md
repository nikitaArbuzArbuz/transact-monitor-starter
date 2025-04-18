# Transaction monitoring starter
Стартер для автоматического логирования всех транзакций микросервиса
## Подключение и настройка
1. Добавьте зависимость в pom.xml
```xml
    <groupId>ru.vtb.transactmonitor</groupId>
    <artifactId>transact-monitor-starter</artifactId>
    <version>1.1.9</version>
```
2. Пропишите настройки в application.yaml
```yaml
transaction-monitoring:
  enabled: true # Флаг включения/выключения