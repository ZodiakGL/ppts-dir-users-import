package ru.sfera.users.config;

import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class DbTestConfiguration {

    @Bean(destroyMethod = "close")
    public PostgreSQLContainer<?> postgres() {
        return PostgreSQL.getInstance();
    }

    @Primary
    @Bean(destroyMethod = "close")
    @DependsOn(value = {"postgres"})
    public HikariDataSource dataSource(PostgreSQLContainer<?> postgres) {
        var ds = new HikariDataSource();
        ds.setJdbcUrl(postgres.getJdbcUrl());
        ds.setPassword(postgres.getPassword());
        ds.setUsername(postgres.getUsername());
        ds.setDriverClassName(Driver.class.getCanonicalName());
        return ds;
    }
}
