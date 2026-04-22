package ru.sfera.users.dir.config.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@Getter
@ConfigurationProperties(prefix = "sfera.import.users.web-clients")
public class DirWebClientProperties {

    private int connectionTimeout;
    private int responseTimeout;
    private boolean debug;
    private boolean sslEnabled;
    private SslContextProperties ssl;
    private String getUsersForGroupEndpoint;

    public record SslContextProperties(String trustedStore, String trustedStorePassword) {}

}
