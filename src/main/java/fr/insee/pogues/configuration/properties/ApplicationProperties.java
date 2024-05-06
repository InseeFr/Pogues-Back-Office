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
        String title,
        String description,
        String[] publicUrls,
        @NotEmpty(message = "cors origins must be specified")
        List<String> corsOrigins) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationProperties that = (ApplicationProperties) o;
        return Objects.equals(host, that.host)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Arrays.equals(publicUrls, that.publicUrls);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(host, title, description);
        result = 31 * result + Arrays.hashCode(publicUrls);
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "host='" + host + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publicUrls=" + Arrays.toString(publicUrls) +
                '}';
    }
}