package fr.insee.pogues.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = { "fr.insee.pogues" })
@EnableTransactionManagement
@Slf4j
public class AppConfiguration {

    @Autowired
    private ExchangeFilterFunction webClientTokenInterceptor;

    /* Timeout value in seconds */
    @Value("${feature.webclient.timeout}")
    long webClientTimeout;

    @Bean
    public WebClient webClient(
            @Value("${feature.oidc.enabled}") boolean oidcEnabled,
            WebClient.Builder builder) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(webClientTimeout));

        builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient));
        if(oidcEnabled)  builder.filter(webClientTokenInterceptor);
        return builder.build();
    }

}
