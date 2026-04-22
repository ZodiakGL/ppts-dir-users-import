package ru.sfera.users.dir.config.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;


@Slf4j
public class WebClientLoggingFilter {

    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.debug("Request: {} {}", request.method(), request.url());
            request.headers().forEach(
                (name, values) -> values.forEach(
                    value -> log.trace("{} {} = {}", request.logPrefix(), name, value)
                )
            );
            return Mono.just(request);
        });
    }

    public ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.debug("Response: {} {}", response.request().getMethod(), response.statusCode());
            return Mono.just(response);
        });
    }

}