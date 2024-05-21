package fr.insee.pogues.configuration.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationUserHelper implements AuthenticationHelper {
    @Override
    public String getUserToken() {
        if(getAuthenticationPrincipal() instanceof JwtAuthenticationToken auth) {
            return auth.getToken().getTokenValue();
        } else {
            log.warn("Cannot retrieve token for the user.");
            return null;
        }
    }

    @Override
    public Authentication getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}