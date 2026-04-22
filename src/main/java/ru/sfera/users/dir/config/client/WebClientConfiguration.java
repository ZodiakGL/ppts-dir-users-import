package ru.sfera.users.dir.config.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.sfera.users.dir.config.client.properties.DirWebClientProperties;


@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final DirWebClientProperties webClientProperties;

    @Bean(name = "tokenDirWebClient")
    public WebClient tokenDirWebClient(WebClient.Builder webClientBuilder, @Qualifier("dirHttpClient") HttpClient httpClient) {
        return webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filters(filters -> {
                if (webClientProperties.isDebug()) {
                    filters.add(webClientLoggingFilter().logRequest());
                    filters.add(webClientLoggingFilter().logResponse());
                }
            })
            .build();
    }

    @Bean
    public WebClientLoggingFilter webClientLoggingFilter() {
        return new WebClientLoggingFilter();
    }

}
