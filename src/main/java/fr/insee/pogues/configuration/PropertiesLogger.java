package fr.insee.pogues.configuration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Listener Spring loads when Spring Boot is starting on
 * ApplicationEnvironmentPreparedEvent event
 * Display props in logs
 *
 */
@Slf4j
public class PropertiesLogger implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Set<String> hiddenWords = Set.of("password", "pwd", "jeton", "token", "secret");

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        Environment environment = event.getEnvironment();

        log.info("===============================================================================================");
        log.info("                                        Properties                                             ");
        log.info("===============================================================================================");

        ((AbstractEnvironment) environment).getPropertySources().stream()
                .map(propertySource -> {
                    if (propertySource instanceof EnumerablePropertySource) {
                        return ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
                    } else {
                        log.warn(propertySource + " is not an EnumerablePropertySource : impossible to display");
                        return new String[] {};
                    }
                })
                .flatMap(Arrays::stream)
                .distinct()
                .filter(Objects::nonNull)
                .forEach(key -> log.info(key + " = " + resolveValueWithSecretAttribute(key, environment)));
        log.info("============================================================================");

    }

    private static Object resolveValueWithSecretAttribute(String key, Environment environment) {
        if (hiddenWords.stream().anyMatch(key::contains)) {
            return "******";
        }
        return environment.getProperty(key);

    }

}
