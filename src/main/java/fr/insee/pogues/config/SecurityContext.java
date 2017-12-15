package fr.insee.pogues.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.Arrays;

/**
 * Created by acordier on 14/07/17.
 */
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment env;

	@Value("${fr.insee.pogues.authentication}")
	Boolean authentication;

	@Value("${fr.insee.pogues.authentication.mode}")
	String authenticationMode;

	@Value("${fr.insee.pogues.permission.ldap.hostname}")
	String ldapHost;

	@Value("${fr.insee.pogues.permission.ldap.root}")
	String rootDn;

	@Value("${fr.insee.pogues.permission.ldap.user.base}")
	String userDn;

	@Value("${fr.insee.pogues.permission.ldap.unite.base}")
	String groupDn;

	@Autowired
	AnonymousAuthenticatorProvider anonymousAuthProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		if (!authentication) {
			
		} else {
			http.csrf().disable().authorizeRequests().antMatchers("/login*").permitAll().antMatchers("/error*")
					.permitAll().anyRequest().authenticated().and().formLogin().usernameParameter("username")
					.passwordParameter("password").loginPage("/login.jsp").loginProcessingUrl("/login")
					.defaultSuccessUrl("/").failureUrl("/error.jsp");
		}
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		if (!authentication) {
			auth.authenticationProvider(anonymousAuthProvider);
			auth.inMemoryAuthentication().withUser("user").password("pass").roles("USER");
		} else {
			auth.ldapAuthentication().userSearchBase(userDn).userSearchFilter("(uid={0})").groupSearchBase(groupDn)
					.groupSearchFilter("member={0}").contextSource(contextSource()).passwordCompare()
					.passwordAttribute("uid");
		}
	}

	@Bean
	public DefaultSpringSecurityContextSource contextSource() {
		DefaultSpringSecurityContextSource contextSource = null;
		if (authentication) {
			if (authenticationMode.equals("LDAP")) {
				contextSource = new DefaultSpringSecurityContextSource(Arrays.asList(ldapHost), rootDn);
			}
			if (authenticationMode.equals("SAML")) {

				contextSource = new DefaultSpringSecurityContextSource(Arrays.asList(ldapHost), rootDn);
			}
			if (authenticationMode.equals("OAUTH2")) {
				contextSource = new DefaultSpringSecurityContextSource(Arrays.asList(ldapHost), rootDn);
			}
		} else {
			contextSource = new DefaultSpringSecurityContextSource(Arrays.asList(ldapHost), rootDn);
		}

		return contextSource;
	}
}
