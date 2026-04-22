package ru.sfera.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.sfera.users.repository.JdbcColorRepository;
import ru.sfera.users.repository.JdbcUserRepository;


@Configuration
public class PersistenceConfig {

    @Bean
    public JdbcColorRepository jdbcColorRepository(
        JdbcTemplate jdbcTemplate,
        @Value("${sfera.import.users.table-mapping.colors-table}") String colorsTable
    ) {
        return new JdbcColorRepository(jdbcTemplate, colorsTable);
    }

    @Bean
    public JdbcUserRepository jdbcUserRepository(
        JdbcTemplate jdbcTemplate,
        @Value("${sfera.import.users.table-mapping.users-table}") String usersTable
    ) {
        return new JdbcUserRepository(jdbcTemplate, usersTable);
    }

}
