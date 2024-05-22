package fr.insee.pogues.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.roles")
public record RoleProperties(
        String designer,
        String admin
) {
}