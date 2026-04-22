package ru.sfera.users.dir.clients;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import ru.sfera.users.dir.config.client.OAuth2WebClientFactory;
import ru.sfera.users.dir.config.client.properties.DirWebClientProperties;
import ru.sfera.users.dir.config.properties.ConnectionProperties;
import ru.sfera.users.dir.config.properties.DirProperties;
import ru.sfera.users.dir.dto.DirUserResponseDto;
import ru.sfera.users.dir.dto.DirUsersRequestDto;


@Slf4j
@RequiredArgsConstructor
public class DirClient {

    private final OAuth2WebClientFactory webClientFactory;
    private final DirWebClientProperties webClientProperties;

    public DirUserResponseDto getUsers(DirUsersRequestDto request, DirProperties properties) {
        log.info("Request to DIR for group: {}", request.getGroupCode());
        return getWebClient(properties.getConnection())
            .method(HttpMethod.PUT)
            .uri(properties.getConnection().getUrl() + webClientProperties.getGetUsersForGroupEndpoint())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(DirUserResponseDto.class)
            .retryWhen(Retry.backoff(
                    properties.getImports().getRetryMaxAttempts(),
                    Duration.ofMillis(properties.getImports().getRetryMinBackOffMillis())
                )
                .filter(throwable -> {
                        if (shouldRetry(throwable)) {
                            log.error("Request to DIR failed, retrying", throwable);
                            return true;
                        }
                        return false;
                    }
                ))
            .doOnError(e -> log.error("Request to DIR failed", e))
            .block();
    }

    private WebClient getWebClient(ConnectionProperties properties) {
        return webClientFactory.getWebClient(properties);
    }

    private boolean shouldRetry(Throwable throwable) {
        return throwable instanceof WebClientResponseException responseEx &&
            responseEx.getStatusCode().is5xxServerError();
    }

}
