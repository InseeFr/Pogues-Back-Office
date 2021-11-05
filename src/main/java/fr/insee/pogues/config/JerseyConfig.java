package fr.insee.pogues.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig {

    @Bean
    public ResourceConfig jerseyResourceConfig(){
        return (new ResourceConfig()).packages("fr.insee.pogues.webservice.rest","fr.insee.pogues.utils.jersey","io.swagger.v3.jaxrs2.integration.resources");
    }

}
