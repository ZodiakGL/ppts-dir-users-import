package ru.sfera.users.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQL {

    private static final Logger logger = LoggerFactory.getLogger(PostgreSQL.class);

    private static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>(
                DockerImageName
                        .parse("postgres:14.5")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("app")
                .withUsername("core")
                .withPassword("core")
                .withReuse(true)
                .withInitScript("postgres-init.sql")
                .waitingFor(Wait.forListeningPort());
        try {
            postgreSQLContainer.start();
        } catch (final Exception ex) {
            logger.error("Error starting Postgres", ex);
            throw ex;
        }
    }

    public static PostgreSQLContainer<?> getInstance() {
        return postgreSQLContainer;
    }
}
