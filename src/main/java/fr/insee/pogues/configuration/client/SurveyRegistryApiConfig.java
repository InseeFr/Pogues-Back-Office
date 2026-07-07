package fr.insee.pogues.configuration.client;

import fr.insee.pogues.client.interceptor.AuthInterceptor;
import fr.insee.pogues.configuration.auth.user.AuthenticationHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@AllArgsConstructor
public class SurveyRegistryApiConfig {

    private AuthenticationHelper authenticationHelper;

    @Bean("surveyRegistryApiRestClient")
    public RestClient surveyRegistryApiRestClient(
            @Value("${application.survey-registry.host}") String registryHost
    ) {
        // ensure that baseUrl start with /
        String baseUrl = UriComponentsBuilder.fromUriString(registryHost)
                .path("/")
                .build()
                .toUriString();

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new AuthInterceptor(authenticationHelper))
                .build();
    }

}