package fr.insee.pogues.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Validated
@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        String host,
        String scheme,
        String title,
        String description,
        String[] publicUrls,
        @NotEmpty(message = "cors origins must be specified")
        List<String> corsOrigins,
        List<String> externalSecureUrls) {
}