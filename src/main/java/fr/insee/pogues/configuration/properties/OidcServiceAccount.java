package fr.insee.pogues.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature.oidc.service-account")
public record OidcServiceAccount (
        boolean enabled,
        String authServerUrl,
        String realm,
        String clientId,
        String clientSecret,
        long refreshMargin){
}
