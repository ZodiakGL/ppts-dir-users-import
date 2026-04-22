package ru.sfera.users.dir.config.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.WebClientReactivePasswordTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.sfera.users.dir.config.client.properties.DirWebClientProperties;
import ru.sfera.users.dir.config.properties.ConnectionProperties;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class OAuth2WebClientFactory implements ClientFactory {

    private final WebClient.Builder builder;
    private final WebClientLoggingFilter loggingFilter;
    private final HttpClient dirHttpClient;
    private final WebClient tokenDirWebClient;
    private final DirWebClientProperties webClientProperties;
    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final Map<String, WebClient> webClients = new HashMap<>();

    public WebClient getWebClient(ConnectionProperties properties) {
        var key = buildKey(properties.getUsername(), properties.getPassword());
        WebClient webClient = webClients.get(key);
        if (webClient == null) {
            var propertiesRegistration = oAuth2ClientProperties.getRegistration().get(properties.getAuthProfile());
            var propertiesProvider = oAuth2ClientProperties.getProvider().get(properties.getAuthProfile());
            var registration = ClientRegistration.withRegistrationId(properties.getAuthProfile())
                .tokenUri(propertiesProvider.getTokenUri())
                .clientId(propertiesRegistration.getClientId())
                .clientSecret(propertiesRegistration.getClientSecret())
                .userNameAttributeName(properties.getUsername())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .build();
            var clientRegistrationsRepository = new InMemoryReactiveClientRegistrationRepository(registration);
            var authorizedClientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationsRepository);
            var oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                getAuthorizedClientManager(
                    properties.getUsername(),
                    properties.getPassword(),
                    clientRegistrationsRepository,
                    authorizedClientService
                )
            );
            oauth2Client.setDefaultClientRegistrationId(properties.getAuthProfile());
            if (webClientProperties.isDebug()) {
                builder.filter(loggingFilter.logRequest());
                builder.filter(loggingFilter.logResponse());
            }
            builder.clientConnector(new ReactorClientHttpConnector(dirHttpClient));
            webClient = builder.filter(oauth2Client).build();
            webClients.put(key, webClient);
        }
        return webClient;
    }

    private ReactiveOAuth2AuthorizedClientManager getAuthorizedClientManager(
        String username,
        String password,
        ReactiveClientRegistrationRepository clientRegistrationsRepository,
        ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        var reactiveOAuth2AccessTokenResponseClient = new WebClientReactivePasswordTokenResponseClient();
        reactiveOAuth2AccessTokenResponseClient.setWebClient(tokenDirWebClient);
        var authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .password(passGrantBuilder -> passGrantBuilder.accessTokenResponseClient(reactiveOAuth2AccessTokenResponseClient))
            .refreshToken()
            .build();

        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationsRepository,
            authorizedClientService
        );
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        authorizedClientManager.setContextAttributesMapper(contextAttributesMapper(username, password));
        return authorizedClientManager;
    }

    private Function<OAuth2AuthorizeRequest, Mono<Map<String, Object>>> contextAttributesMapper(String username, String password) {
        return authorizeRequest -> {
            final Map<String, Object> contextAttributes = new HashMap<>();
            contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
            contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);
            return Mono.just(contextAttributes);
        };
    }

}
