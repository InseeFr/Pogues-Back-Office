package fr.insee.pogues;

import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        KeycloakAutoConfiguration.class,
        WebMvcAutoConfiguration.class})
public class Pogues {

    public static void main(String[] args) {
        SpringApplication.run(Pogues.class, args);
    }

}
