package fr.insee.pogues.configuration.auth.user;

import org.springframework.security.core.Authentication;

public interface AuthenticationHelper {
    /**
     * Retrieve the auth token of the current user
     *
     * @return auth token
     */
    String getUserToken();

    /**
     * Retrieve the authentication principal for current user
     *
     * @return {@link Authentication} the authentication user object
     */
    Authentication getAuthenticationPrincipal();
}