package fr.insee.pogues.config.auth.security;

import fr.insee.pogues.config.auth.UserProvider;
import fr.insee.pogues.config.auth.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "fr.insee.pogues.authentication", havingValue = "OIDC")
public class OpenIDConnectSecurityContext extends WebSecurityConfigurerAdapter {

	@Value("${fr.insee.pogues.force.ssl}")
	boolean requireSSL;

	static final Logger logger = LogManager.getLogger(OpenIDConnectSecurityContext.class);
	@Value("${jwt.stamp-claim}")
	private String stampClaim;
	@Value("${jwt.username-claim}")
	private String nameClaim;
	@Value("${fr.insee.pogues.cors.allowedOrigin}")
	private Optional<String> allowedOrigin;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//TODO : variabiliser path /api...
		http.sessionManagement().disable();
		http.cors(withDefaults())
				.authorizeRequests()
				.antMatchers("/api/init", "/api/healthcheck").permitAll()
				.antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.antMatchers("/api/persistence/questionnaire/json-lunatic/**").permitAll()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				.anyRequest().authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt();
		if (requireSSL)
			http.antMatcher("/**").requiresChannel().anyRequest().requiresSecure();
	}

	@Bean
	public UserProvider getUserProvider() {
		return auth -> {
			final Jwt jwt = (Jwt) auth.getPrincipal();
			return new User(jwt.getClaimAsString(stampClaim), jwt.getClaimAsString(nameClaim));
		};
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.addExposedHeader("Content-Disposition");
		configuration.addAllowedOrigin("*");
		UrlBasedCorsConfigurationSource source = new
				UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
