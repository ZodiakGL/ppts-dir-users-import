package ru.sfera.users.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.sfera.users.dir.clients.DirClient;
import ru.sfera.users.dir.config.client.HttpClientConfiguration;
import ru.sfera.users.dir.config.client.OAuth2WebClientFactory;
import ru.sfera.users.dir.config.client.WebClientConfiguration;
import ru.sfera.users.dir.config.client.properties.DirWebClientProperties;
import ru.sfera.users.dir.config.properties.UsersImportProperties;
import ru.sfera.users.dir.service.DirUserService;


@Import(
    {
        HttpClientConfiguration.class,
        WebClientConfiguration.class,
        OAuth2WebClientFactory.class
    }
)
@Configuration
@EnableConfigurationProperties({UsersImportProperties.class})
public class DirConfig {

    @Bean
    public DirClient dirClient(OAuth2WebClientFactory webClientFactory, DirWebClientProperties webClientProperties) {
        return new DirClient(webClientFactory, webClientProperties);
    }

    @Bean
    public DirUserService dirUserService(DirClient dirClient, UsersImportProperties importProperties) {
        return new DirUserService(dirClient, importProperties);
    }

}
