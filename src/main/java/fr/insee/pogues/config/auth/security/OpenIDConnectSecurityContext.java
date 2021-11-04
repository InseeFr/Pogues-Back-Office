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

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "fr.insee.pogues.authentication", havingValue = "OIDC")
public class OpenIDConnectSecurityContext extends WebSecurityConfigurerAdapter {

	@Value("${fr.insee.pogues.force.ssl}")
	boolean requireSSL;

	final static Logger logger = LogManager.getLogger(OpenIDConnectSecurityContext.class);
	@Value("${jwt.stamp-claim}")
	private String stampClaim;
	@Value("${jwt.username-claim}")
	private String nameClaim;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		if (requireSSL)
			http.antMatcher("/**").requiresChannel().anyRequest().requiresSecure();
		//TODO : variabiliser path /api...
		http.sessionManagement().disable();
		http.authorizeRequests().antMatchers("/api/init").permitAll()
				.antMatchers("/api/healthcheck").permitAll()
				.antMatchers("/swagger-ui/**").permitAll()
				.antMatchers("/api/openapi.json").permitAll()
				.antMatchers("/api/persistence/questionnaire/json-lunatic/**").permitAll()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				.anyRequest().authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt();;
	}

	@Bean
	public UserProvider getUserProvider() {
		return auth -> {
			final Jwt jwt = (Jwt) auth.getPrincipal();
			return new User(jwt.getClaimAsString(stampClaim), jwt.getClaimAsString(nameClaim));
		};
	}


}
