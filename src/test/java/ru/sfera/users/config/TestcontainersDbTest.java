package ru.sfera.users.config;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;


@ActiveProfiles("test")
@SpringBootTest(classes = {DbTestConfiguration.class})
@ContextConfiguration(initializers = WireMockInitializer.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableAutoConfiguration
public abstract class TestcontainersDbTest {

}
