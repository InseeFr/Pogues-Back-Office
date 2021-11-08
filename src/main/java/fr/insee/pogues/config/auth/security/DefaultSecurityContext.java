package fr.insee.pogues.config.auth.security;

import fr.insee.pogues.config.auth.UserProvider;
import fr.insee.pogues.config.auth.user.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ConditionalOnExpression("!'OIDC'.equals('${fr.insee.pogues.authentication}')")
public class DefaultSecurityContext extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().anyRequest().permitAll();
//		if (Config.REQUIRES_SSL) {
//			http.antMatcher("/**").requiresChannel().anyRequest().requiresSecure();
//		}
	}

	@Bean
	public UserProvider getUserProvider() {
		return auth->new User();
	}

	
}
