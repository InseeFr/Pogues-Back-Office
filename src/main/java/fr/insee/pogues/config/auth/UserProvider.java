package fr.insee.pogues.config.auth;

import fr.insee.pogues.config.auth.user.User;
import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface UserProvider {

    User getUser(Authentication authentication);

}
