package fr.insee.pogues.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
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
@Slf4j
public class PropertiesLogger implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Set<String> hiddenWords = Set.of("password", "pwd", "jeton", "token", "secret");

    @EventListener
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent  event) {
        log.info("                 Logging environment variables started");
        log.info("============================================================================");
        log.info("                                Hardware");
        log.info("============================================================================");
        log.info("    ---->    Java memory");
        Runtime runtime = Runtime.getRuntime();
        final long mb = 1024 * 1024;
        final long maxMemoryInMb = runtime.maxMemory() / mb;
        final long allocatedMemoryInMb = runtime.totalMemory() / mb;
        final long freeMemoryInMb = runtime.freeMemory() / mb;
        int maxStrLength = String.valueOf(maxMemoryInMb).length();
        log.info("+--------------------------------+-----{}+", "-".repeat(maxStrLength));
        log.info("| Type of memory                 | Size{}|", " ".repeat(maxStrLength));
        log.info("|--------------------------------|-----{}|", "-".repeat(maxStrLength));
        log.info("| Current Free memory            | {}{} MB |",
                " ".repeat(maxStrLength-String.valueOf(freeMemoryInMb).length()),
                freeMemoryInMb);
        log.info("| Current Allocated memory       | {}{} MB |",
                " ".repeat(maxStrLength-String.valueOf(allocatedMemoryInMb).length()),
                allocatedMemoryInMb);
        log.info("| Current total Free memory      | {}{} MB |",
                " ".repeat(maxStrLength-String.valueOf(freeMemoryInMb + (maxMemoryInMb - allocatedMemoryInMb)).length()),
                freeMemoryInMb + (maxMemoryInMb - allocatedMemoryInMb));
        log.info("| Max available memory for JVM   | {} MB |", maxMemoryInMb);
        log.info("+--------------------------------+-----{}+", "-".repeat(maxStrLength));
        log.info("    ---->    CPU");
        log.info(" Available CPUs : {}", runtime.availableProcessors());
        log.info("============================================================================");
        log.info("                               Properties");
        log.info("============================================================================");
        Environment environment = event.getEnvironment();

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
        log.info("                  Logging environment variables ended                       ");
    }

    private static Object resolveValueWithSecretAttribute(String key, Environment environment) {
        if (hiddenWords.stream().anyMatch(key::contains)) {
            return "******";
        }
        return environment.getProperty(key);

    }
}
