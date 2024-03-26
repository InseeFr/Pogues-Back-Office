package fr.insee.pogues.configuration;

import fr.insee.pogues.configuration.rest.AuthenticationHelper;
import fr.insee.pogues.configuration.rest.WebClientTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = { "fr.insee.pogues" })
@EnableTransactionManagement
@EnableCaching
@Slf4j
public class AppConfiguration {

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Bean
    public WebClient webClient(
            @Value("${feature.oidc.enabled}") boolean oidcEnabled,
            WebClient.Builder builder) {
        builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        if(oidcEnabled)  builder.filter(new WebClientTokenInterceptor(authenticationHelper));
        return builder.build();
    }

}
