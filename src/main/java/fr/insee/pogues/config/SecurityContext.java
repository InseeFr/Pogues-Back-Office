package fr.insee.pogues.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import java.util.Arrays;

/**
 * Created by acordier on 14/07/17.
 */
@Configuration
@EnableWebSecurity
@PropertySource("classpath:${fr.insee.pogues.env:prod}/pogues-bo.properties")
public class SecurityContext extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/login.jsp")
                .loginProcessingUrl("/j_spring_security_check")
                .defaultSuccessUrl("/")
                .failureUrl("/error.jsp");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userSearchBase("ou=Personnes")
                .userSearchFilter("(uid={0})")
                .groupSearchBase("ou=groups")
                .groupSearchFilter("member={0}")
                .contextSource(contextSource())
                .passwordCompare().passwordAttribute("uid");
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        String ldapHost = env.getProperty("fr.insee.pogues.permission.ldap.hostname");
        String rootDn = env.getProperty("fr.insee.pogues.permission.ldap.root");
        return  new DefaultSpringSecurityContextSource(Arrays.asList(ldapHost), rootDn);
    }
}
