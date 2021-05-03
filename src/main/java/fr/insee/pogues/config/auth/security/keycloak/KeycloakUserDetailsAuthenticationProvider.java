package fr.insee.pogues.config.auth.security.keycloak;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;

import fr.insee.pogues.config.auth.user.User;

public class KeycloakUserDetailsAuthenticationProvider extends KeycloakAuthenticationProvider {

	final static Logger logger = LogManager.getLogger(KeycloakUserDetailsAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication authentication) {
		final KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) super.authenticate(authentication);
		if (token == null) {
			return null;
		}

		final User user = new User();

		final Map<String, Object> otherClaims = token.getAccount().getKeycloakSecurityContext().getToken()
				.getOtherClaims();
		user.setStamp((String) otherClaims.getOrDefault("timbre", "default stamp"));

		String userId = token.getAccount().getKeycloakSecurityContext().getToken().getPreferredUsername();
		logger.info("User {} connected with stamps {}", userId, user.getStamp());

		return new KeycloakUserDetailsAuthenticationToken(user, token.getAccount(), token.getAuthorities());
	}

}
