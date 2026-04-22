package ru.sfera.users.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.sfera.users.service.ImportSchedulerService;
import ru.sfera.users.service.ImportService;


@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "${sfera.import.users.shedlock.default-lock-at-most-for:PT2H}")
@ConditionalOnProperty(name = "sfera.import.users.scheduler.enabled", havingValue = "true")
public class UserImportSchedulingConfig {

    @Bean
    @ConditionalOnMissingBean(LockProvider.class)
    public LockProvider userImportShedlockLockProvider(
        JdbcTemplate jdbcTemplate,
        @Value("${sfera.import.users.shedlock.table:shedlock}") String shedlockTableName
    ) {
        return new JdbcTemplateLockProvider(jdbcTemplate, shedlockTableName);
    }

    @Bean
    public ImportSchedulerService importSchedulerService(ImportService importService) {
        return new ImportSchedulerService(importService);
    }

}
