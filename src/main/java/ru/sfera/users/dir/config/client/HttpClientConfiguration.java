package ru.sfera.users.dir.config.client;

import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.time.Duration;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import reactor.netty.http.client.HttpClient;
import ru.sfera.users.dir.config.client.properties.DirWebClientProperties;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DirWebClientProperties.class)
public class HttpClientConfiguration {

    private final DirWebClientProperties webClientProperties;

    @Bean(name = "dirHttpClient")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HttpClient dirHttpClient() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                webClientProperties.getConnectionTimeout())
            .responseTimeout(Duration.ofMillis(
                webClientProperties.getResponseTimeout()));
        if (webClientProperties.isSslEnabled()) {
            return httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(createSslContext()));
        }
        return httpClient;
    }

    private SslContext createSslContext() {
        try (var fis = new FileInputStream(webClientProperties.getSsl().trustedStore())) {
            var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fis, webClientProperties.getSsl().trustedStorePassword().toCharArray());
            var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            return SslContextBuilder.forClient()
                .trustManager(trustManagerFactory)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating SSL context. ", e);
        }
    }

}
