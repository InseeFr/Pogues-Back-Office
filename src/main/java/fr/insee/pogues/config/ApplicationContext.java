package fr.insee.pogues.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:env/${fr.insee.pogues.env:dv}/pogues-bo.properties")
public class ApplicationContext { }
