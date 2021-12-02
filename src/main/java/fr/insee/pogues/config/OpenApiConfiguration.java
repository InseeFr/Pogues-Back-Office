package fr.insee.pogues.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfiguration {

	@Value("${fr.insee.pogues.api.scheme}")
	private String apiScheme;

	@Value("${fr.insee.pogues.api.host}")
	private String apiHost;

	@Value("${fr.insee.pogues.model.version}")
	private String poguesModelVersion;

	@Value("${fr.insee.pogues.version}")
	private String projectVersion;

	@Bean
	public OpenAPI customOpenAPI() {
		Server server = new Server();
		server.setUrl(apiScheme + "://" + apiHost);
		OpenAPI openAPI = new OpenAPI().addServersItem(server).info(new Info().title("Pogues API")
				.description(
						"Rest Endpoints and services used by Pogues"
						+ "<h3>Pogues-Model version : " + poguesModelVersion + "</h3>")
				.version(projectVersion)
		);
		return openAPI;
	}
}
