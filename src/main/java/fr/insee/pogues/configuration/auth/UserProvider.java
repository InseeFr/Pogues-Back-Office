package fr.insee.pogues.configuration.auth;

import fr.insee.pogues.configuration.auth.user.User;
import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface UserProvider {

    User getUser(Authentication authentication);

}
