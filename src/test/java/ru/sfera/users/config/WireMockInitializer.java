package ru.sfera.users.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;


public class WireMockInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final WireMockServer WIRE_MOCK_SERVER1 = new WireMockServer(new WireMockConfiguration().port(8091));
    public static final WireMockServer WIRE_MOCK_SERVER2 = new WireMockServer(new WireMockConfiguration().port(8092));

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        WIRE_MOCK_SERVER1.start();
        WIRE_MOCK_SERVER2.start();

        applicationContext.addApplicationListener(
            applicationEvent -> {
                if (applicationEvent instanceof ContextClosedEvent) {
                    WIRE_MOCK_SERVER1.stop();
                    WIRE_MOCK_SERVER2.stop();
                }
            });

        applicationContext.getBeanFactory().registerSingleton("wireMockServer1", WIRE_MOCK_SERVER1);
        applicationContext.getBeanFactory().registerSingleton("wireMockServer2", WIRE_MOCK_SERVER2);
    }

}
