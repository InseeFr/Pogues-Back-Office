package fr.insee.pogues.configuration.swagger;

import fr.insee.pogues.configuration.properties.OidcProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@ConditionalOnProperty(value="feature.swagger.enabled", havingValue = "true")
public class SpringDocConfiguration {

    public static final String OAUTH2SCHEME = "oAuth2";
    public static final String BEARERSCHEME = "bearerAuth";

    private static final Logger log = LoggerFactory.getLogger(SpringDocConfiguration.class);
    @Value("${application.pogues-model.version}")
    private String poguesModelVersion;
    @Bean
    @ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "false")
    protected OpenAPI noAuthOpenAPI(BuildProperties buildProperties) {
        return generateOpenAPI(buildProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
    protected OpenAPI oidcOpenAPI(OidcProperties oidcProperties, BuildProperties buildProperties) {
        String authUrl = oidcProperties.authServerUrl() + "/realms/" + oidcProperties.realm() + "/protocol/openid-connect";

        return generateOpenAPI(buildProperties)
                .addSecurityItem(new SecurityRequirement().addList(OAUTH2SCHEME, Arrays.asList("read", "write")))
                .addSecurityItem(new SecurityRequirement().addList(BEARERSCHEME, Arrays.asList("read", "write")))
                .components(
                        new Components()
                                .addSecuritySchemes(OAUTH2SCHEME,
                                        new SecurityScheme()
                                                .name(OAUTH2SCHEME)
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .flows(getFlows(authUrl))

                                )
                                .addSecuritySchemes(BEARERSCHEME,
                                        new SecurityScheme()
                                                .name(BEARERSCHEME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        String.format("You have to retrieve JWT token with oidc server, copy & paste here its value %s",
                                                                oidcProperties.tokenHelper() != null && !oidcProperties.tokenHelper().isEmpty()
                                                                        ? ": --> [Retrieve token here]("+oidcProperties.tokenHelper()+")"
                                                                        : ""))
                                )
                );

    }

    public SpringDocConfiguration(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }

    private OpenAPI generateOpenAPI(BuildProperties buildProperties) {
        return new OpenAPI().info(
                new Info()
                        .title(buildProperties.getName())
                        .description(String.format("""
                                        <h2>Rest Endpoints and services used by Pogues</h2>
                                        <div><b>Pogues-Model version : </b><i>%s</i></div>
                                        """,poguesModelVersion))
                        .version(buildProperties.getVersion())
        );
    }

    private OAuthFlows getFlows(String authUrl) {
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow flow = new OAuthFlow();
        Scopes scopes = new Scopes();
        flow.setAuthorizationUrl(authUrl + "/auth");
        flow.setTokenUrl(authUrl + "/token");
        flow.setRefreshUrl(authUrl + "/token");
        flow.setScopes(scopes);
        return flows.authorizationCode(flow);
    }
}