package fr.insee.pogues.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

public class SwaggerConfig extends ResourceConfig {

	private final static Logger logger = LogManager.getLogger(SwaggerConfig.class);

	private static final String WEBAPPS = "%s/webapps/%s";
	private static final String CATALINA_BASE = "catalina.base";

	public SwaggerConfig(@Context ServletConfig servletConfig) throws IOException {
		super();

		Properties props = getEnvironmentProperties();

		OpenAPI openApi = new OpenAPI();

		Info info = new Info().title("Pogues API").version("1.0")
				.description("Rest Endpoints and services used by Pogues");
		openApi.info(info);

		String swaggerScheme = props.getProperty("fr.insee.pogues.api.scheme");
		String swaggerHost = props.getProperty("fr.insee.pogues.api.host");
		String swaggerBasePath = props.getProperty("fr.insee.pogues.api.name");
		String swaggerUrl = swaggerScheme + "://" + swaggerHost + swaggerBasePath;
		Server server = new Server();
		logger.info("______________________________________________________________________");
		logger.info("____________________SWAGGER HOST : {}_________________________________________________",
				swaggerHost);
		logger.info("____________________SWAGGER BASEPATH : {} _________________________________________________",
				swaggerBasePath);
		logger.info("____________________SWAGGER CONFIG : {} _________________________________________________",
				swaggerUrl);
		logger.info("______________________________________________________________________");
		server.url(swaggerUrl);
		openApi.addServersItem(server);

		SwaggerConfiguration oasConfig = new SwaggerConfiguration().openAPI(openApi)
				.resourcePackages(Stream.of("fr.insee.pogues.webservice.rest").collect(Collectors.toSet()))
				.prettyPrint(true);
		String oasConfigString = oasConfig.toString();
		logger.info("SWAGGER : {}", oasConfigString);

		openApi.components(
				new Components().addSecuritySchemes("bearer-jwt", new SecurityScheme().type(SecurityScheme.Type.HTTP)
						.scheme("bearer").bearerFormat("JWT").in(SecurityScheme.In.HEADER).name("Authorization")));

		openApi.addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));

		OpenApiResource openApiResource = new OpenApiResource();
		openApiResource.setOpenApiConfiguration(oasConfig);
		register(openApiResource);
		register(MultiPartFeature.class);

	}

	private Properties getEnvironmentProperties() throws IOException {
		Properties props = new Properties();
		String env = System.getProperty("fr.insee.pogues.env");
		if (null == env) {
			env = "dev";
		}
		String propsPath = String.format("env/%s/pogues-bo.properties", env);
		props.load(getClass().getClassLoader().getResourceAsStream(propsPath));
		loadPropertiesIfExist(props, "pogues-bo.properties");
		loadPropertiesIfExist(props, "rmspogfo.properties");
		loadPropertiesIfExist(props, "rmespogfo.properties");
		return props;
	}

	private void loadPropertiesIfExist(Properties props, String fileName) throws IOException {
		File f = new File(String.format(WEBAPPS, System.getProperty(CATALINA_BASE), fileName));
		if (f.exists() && !f.isDirectory()) {
			try (FileReader r = new FileReader(f)) {
				props.load(r);
				r.close();
			}
		}
	}

}
