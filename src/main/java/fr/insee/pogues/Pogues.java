package fr.insee.pogues;

import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        KeycloakAutoConfiguration.class,
        WebMvcAutoConfiguration.class})
@ConfigurationPropertiesScan
public class Pogues {

    public static void main(String[] args) {
        SpringApplication.run(Pogues.class, args);
    }

}
