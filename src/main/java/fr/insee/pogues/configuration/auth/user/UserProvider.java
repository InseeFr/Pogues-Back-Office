package fr.insee.pogues.configuration.auth.user;

import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface UserProvider {

    User getUser(Authentication authentication);

}
