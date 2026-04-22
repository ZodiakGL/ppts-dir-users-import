package ru.sfera.users.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.sfera.users.config.DirConfig;
import ru.sfera.users.config.PersistenceConfig;
import ru.sfera.users.config.UserImportConfig;
import ru.sfera.users.config.UserImportSchedulingConfig;


@Configuration
@Import({
    DirConfig.class,
    PersistenceConfig.class,
    UserImportConfig.class,
    UserImportSchedulingConfig.class
})
@ConditionalOnProperty(name = "sfera.import.users.enabled", havingValue = "true")
public class UserImportAutoConfiguration {}
