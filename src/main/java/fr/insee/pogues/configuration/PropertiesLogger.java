package fr.insee.pogues.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Listener Spring loads when Spring Boot is starting on
 * ApplicationEnvironmentPreparedEvent event
 * Display props in logs
 *
 */
@Component
@Slf4j
public class PropertiesLogger {

    private static final Set<String> hiddenWords = Set.of("password", "pwd", "jeton", "token", "secret");

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        log.info("===============================================================================================");
        log.info("                                       Java memory                                             ");
        log.info("===============================================================================================");
        Runtime runtime = Runtime.getRuntime();
        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long mb = 1024 * 1024;
        log.info("Free memory: {} MB", (freeMemory / mb));
        log.info("Allocated memory: {} MB", (allocatedMemory / mb));
        log.info("Max memory: {} MB", (maxMemory / mb));
        log.info("Total free memory: {} MB",((freeMemory + (maxMemory - allocatedMemory)) / mb));

        Environment environment = event.getApplicationContext().getEnvironment();
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
