package fr.insee.pogues.configuration.auth;

import fr.insee.pogues.configuration.auth.user.User;
import fr.insee.pogues.configuration.properties.ApplicationProperties;
import fr.insee.pogues.configuration.properties.OidcProperties;
import fr.insee.pogues.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.net.URI;
import java.util.Collection;

/**
 * Spring security configuration when using OIDC auth
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
@AllArgsConstructor
public class OidcSecurityConfiguration {
    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    /**
     * Configure spring security filter chain to handle OIDC authentication
     *
     * @param http Http Security Object
     * @return the spring security filter
     * @throws Exception exception
     */

    @Bean
    @Order(2)
    protected SecurityFilterChain filterChain(HttpSecurity http,
                                              OidcProperties oidcProperties, RoleProperties roleProperties) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                        .contentSecurityPolicy(cspConfig -> cspConfig
                                .policyDirectives("default-src 'none'")
                        )
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        ))
                .authorizeHttpRequests(configurer -> configurer
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(oidcProperties, roleProperties))))
                .build();
    }

    @Bean
    @Order(1)
    protected SecurityFilterChain filterPublicUrlsChain(HttpSecurity http, ApplicationProperties applicationProperties,
                                                        OidcProperties oidcProperties) throws Exception {
        URI authServerUrl = URI.create(oidcProperties.authServerUrl());
        String authServerHost = String.format("%s://%s",authServerUrl.getScheme(),authServerUrl.getHost());
        String authorizedConnectionHost = oidcProperties.enabled() ?
                " " + authServerHost : "";
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, applicationProperties.publicUrls(), authorizedConnectionHost);
    }

    @Bean
    protected JwtAuthenticationConverter jwtAuthenticationConverter(OidcProperties oidcProperties, RoleProperties roleProperties) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName(oidcProperties.principalAttribute());
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter(oidcProperties, roleProperties));
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter(OidcProperties oidcProperties, RoleProperties roleProperties) {
        return new GrantedAuthorityConverter(oidcProperties, roleProperties);
    }

    @Bean
    public UserProvider getUserProvider(OidcProperties oidcProperties) {
        return auth -> {
            final Jwt jwt = (Jwt) auth.getPrincipal();
            return new User(
                    jwt.getClaimAsString(oidcProperties.stampClaim()),
                    jwt.getClaimAsString(oidcProperties.usernameClaim()),
                    jwt.getClaimAsString(oidcProperties.principalAttribute()));
        };
    }

}
