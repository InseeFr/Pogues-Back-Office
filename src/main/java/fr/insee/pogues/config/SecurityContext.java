package fr.insee.pogues.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	@Value("${fr.insee.pogues.force.ssl}")
	Boolean requiresSSL;

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

		if (authentication) {
			http.csrf().disable().authorizeRequests()
					.antMatchers("/login*").permitAll()
					.antMatchers("/error*").permitAll()
					.anyRequest().authenticated().and().formLogin().usernameParameter("username")
					.passwordParameter("password").loginPage("/login.jsp").loginProcessingUrl("/login")
					.defaultSuccessUrl("/").failureUrl("/error.jsp");
		}

		if (requiresSSL) {
			http.antMatcher("/**").requiresChannel().anyRequest().requiresSecure().and().csrf().disable();;
		} else {
			http.csrf().disable();
		}

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		if (authentication) {
			if (authenticationMode.equals("LDAP")) {
				auth.ldapAuthentication().userSearchBase(userDn).userSearchFilter("(uid={0})").groupSearchBase(groupDn)
						.groupSearchFilter("member={0}").contextSource(contextSource()).passwordCompare()
						.passwordAttribute("uid");
			}
			if (authenticationMode.equals("SAML")) {
				auth.ldapAuthentication().userSearchBase(userDn).userSearchFilter("(uid={0})").groupSearchBase(groupDn)
						.groupSearchFilter("member={0}").contextSource(contextSource()).passwordCompare()
						.passwordAttribute("uid");
			}
			if (authenticationMode.equals("OAUTH2")) {
				auth.ldapAuthentication().userSearchBase(userDn).userSearchFilter("(uid={0})").groupSearchBase(groupDn)
						.groupSearchFilter("member={0}").contextSource(contextSource()).passwordCompare()
						.passwordAttribute("uid");
			}
		} else {
			auth.authenticationProvider(anonymousAuthProvider);
			auth.inMemoryAuthentication().withUser("Guest").password("Guest").roles("TEST");
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
