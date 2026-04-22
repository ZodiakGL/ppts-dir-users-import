# Стартер для импорта пользователей из групп ДиР

## Подключение и настройка модуля

Для подключения модуля к приложению необходимо добавить зависимость

```groovy
implementation "ru.sfera.users:sfera-dir-users-import:${dirUsersImport}"
```

Далее потребуется прописать следующие группы настроек:

* Базовые настройки:
    * `sfera.import.users.enabled` - Параметр подключения автоконфигурации стартера импорта;
    * `sfera.import.users.system-code` - Код системы, от которой созданы группы в ДиР;
    * `sfera.import.users.ignore-users` - Пользователи, уже существующие в системе, которых необходимо игнорировать при импорте;
    * `sfera.import.users.mark-users-as-inactive` - Признак пометки пользователей как неактивных;
    * `sfera.import.users.user-iteration-part` - Размер батча при разбивке пользователей для пакетной обработки ролей;
    * `sfera.import.users.continue-on-error` - Признак необходимости продолжения импорта при ошибках обработки ролей;
    * `sfera.import.users.delete-user-roles` - Признак необходимости удаления лишних ролей у пользователя при импорте;
    * `sfera.import.users.table-mapping.colors-table` - Таблица с палитрой цветов;
    * `sfera.import.users.table-mapping.users-table` - Таблица с пользователями;
* Настройки джобы импорта:
    * `sfera.import.users.scheduler.enabled` - Параметр включения джобы импорта с периодичностью;
    * `sfera.import.users.scheduler.fixed-delay` - Задержка джобы между запусками;
    * `sfera.import.users.scheduler.initial-delay` - Задержка джобы после старта приложения;
* Распределённая блокировка джобы (ShedLock, чтобы при нескольких инстансах импорт не выполнялся параллельно):
    * Таблица `shedlock` в БД должна быть создана миграциями приложения / платформы (в стартере DDL не поставляется); при нетипичной схеме укажите полное имя в `sfera.import.users.shedlock.table` (например, `myschema.shedlock`);
    * `sfera.import.users.shedlock.table` — полное имя таблицы для ShedLock (по умолчанию `shedlock` в схеме по умолчанию подключения);
    * `sfera.import.users.shedlock.name` — имя lock в БД;
    * `sfera.import.users.shedlock.lock-at-least-for` / `sfera.import.users.shedlock.lock-at-most-for` — ISO-8601 длительности; при длительном импорте увеличьте `lock-at-most-for`;
    * `sfera.import.users.shedlock.default-lock-at-most-for` — дефолт `defaultLockAtMostFor` у `@EnableSchedulerLock`;
    * Если в приложении уже объявлен свой `LockProvider` (другой модуль), бин стартера не создаётся (`@ConditionalOnMissingBean`);
* Настройки WebClient, обращающегося к ДиР:
    * `sfera.import.users.web-clients.debug` - Флаг отладки приёма/передачи данных по HTTP(S);
    * `sfera.import.users.web-clients.ssl-enabled` - Флаг использования SSL для приёма/передачи данных по HTTP(S);
    * `sfera.import.users.web-clients.connection-timeout` - Таймаут при подключении;
    * `sfera.import.users.web-clients.response-timeout` - Таймаут при ожидании ответа;
    * `sfera.import.users.web-clients.get-users-for-group-endpoint` - Эндпоинт ДиРа для получения пользователей группы;
    * Настройки SSL:
        * `sfera.import.users.web-clients.ssl.trusted-store` - Путь к файлу сертификатов trust store;
        * `sfera.import.users.web-clients.ssl.trusted-store-password` - Пароль к файлу сертификатов trust store;
* Настройки ДиР:
    * `sfera.import.users.dir[i].enabled` - Флаг включения i-го подключения к ДиР, по-умолчанию true;
    * Настройки подключения:
      * `sfera.import.users.dir[i].connection.username` - Логин системного пользователя ДиР для i подключения;
      * `sfera.import.users.dir[i].connection.password` - Логин системного пользователя ДиР для i подключения;
      * `sfera.import.users.dir[i].connection.url` - Адрес ДиР для i подключения;
      * `sfera.import.users.dir[i].connection.auth-profile` - Имя профиля для OAuth2-авторизации;
    * Настройки пейджинга и ретраев:
      * `sfera.import.users.dir[i].imports.page-size` - Размер батча запроса пользователей по группе;
      * `sfera.import.users.dir[i].imports.retry-min-back-off-millis` - Минимальную задержка в миллисекундах перед повторной попыткой выполнения запроса в ДиР после сбоя;
      * `sfera.import.users.dir[i].imports.retry-max-attempts` - Максимальное количество повторных попыток запроса ДиР;
      * Настройки маппинга ролей и групп ДиР:
          * `sfera.import.users.dir[i].imports.roles[j].priority` - Приоритет обработки j роли для i подключения;
          * `sfera.import.users.dir[i].imports.roles[j].name` - Имя j-ой роли;
          * `sfera.import.users.dir[i].imports.roles[j].dir-groups` - Коды групп j-ой роли в ДиР;
          * `sfera.import.users.dir[i].imports.roles[j].excl-dir-groups` - Коды групп которые нужно исключить, если пользователь находится в текущей группе;


### Пример настроек

```yaml
sfera:
  import:
    users:
      enabled: false
      system-code: SYS
      scheduler:
        enabled: false
        fixed-delay: PT30M
        initial-delay: PT5S
      shedlock:
        table: shedlock
        name: sfera_dir_users_import
        lock-at-least-for: PT1M
        lock-at-most-for: PT2H
        default-lock-at-most-for: PT2H
      ignore-users: sfera_tech_user
      mark-users-as-inactive: true
      user-iteration-part: 100
      continue-on-error: false
      delete-user-roles: true
      table-mapping:
        colors-table: schema_name.palette_colors
        users-table: schema_name.users
      web-clients:
        debug: false
        ssl-enabled: false
        ssl:
          trusted-store: ./trust.p12
          trusted-store-password: changeit
        connection-timeout: 3000
        response-timeout: 45000
        get-users-for-group-endpoint: /api/v1/public/groups/users/search
      dir:
        -
          enabled: true
          connection:
            username: user
            password: password
            url: http:://localhost:8080/app/ppam
            auth-profile: sfera-profile
          imports:
            page-size: 100
            retry-min-back-off-millis: 1000
            retry-max-attempts: 3
            roles:
              -
                priority: 1
                name: ROLE_1
                dirGroups:
                  - group1
                exclDirGroups:
                  - group2
```
