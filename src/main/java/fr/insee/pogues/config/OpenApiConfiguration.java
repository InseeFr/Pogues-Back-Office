package fr.insee.pogues.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "bearer"
	)
public class OpenApiConfiguration {

	@Value("${fr.insee.pogues.api.scheme}")
	private String apiScheme;

	@Value("${fr.insee.pogues.api.host}")
	private String apiHost;
	
	@Value("${fr.insee.pogues.api.name}")
	private String apiName;

	@Value("${fr.insee.pogues.model.version}")
	private String poguesModelVersion;

	@Value("${fr.insee.pogues.version}")
	private String projectVersion;

	@Bean
	public OpenAPI customOpenAPI() {
		Server server = new Server();
		server.setUrl(String.format("%s://%s%s", apiScheme, apiHost, apiName));
		return new OpenAPI()
				.addServersItem(server)
				.info(new Info()
				.title("Pogues API")
				.description(
						"Rest Endpoints and services used by Pogues"
						+ "<h3>Pogues-Model version : " + poguesModelVersion + "</h3>")
				.version(projectVersion)
				
		);
	}
}
