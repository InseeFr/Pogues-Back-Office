package fr.insee.pogues.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:${fr.insee.pogues.env:prod}/pogues-bo.properties")
public class ApplicationContext {
}
