package fr.insee.pogues.client.interceptor;

import fr.insee.pogues.configuration.properties.ApplicationProperties;
import fr.insee.pogues.configuration.properties.OidcServiceAccount;
import fr.insee.pogues.configuration.auth.user.AuthenticationHelper;
import fr.insee.pogues.exception.oidc.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebClientTokenInterceptor implements ExchangeFilterFunction {

    private static final String ACCESS_TOKEN = "access_token";

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private OidcServiceAccount serviceAccountProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    private OidcToken serviceAccountToken = null;


    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // remove empty secured urls
        boolean needUserToken = applicationProperties.externalSecureUrls().stream()
                .filter(Predicate.not(String::isBlank)).anyMatch(secureUrl -> request.url().toString().contains(secureUrl));

        // remove empty secured urls
        boolean needServiceAccountToken = applicationProperties.externalSecureUrlsWithServiceAccount().stream()
                .filter(Predicate.not(String::isBlank)).anyMatch(secureUrl -> request.url().toString().contains(secureUrl));

        if(needUserToken){
            log.debug("User tokenValue is necessary to call URI :"+request.url());
            ClientRequest newRequest = ClientRequest.from(request)
                    .headers(h -> h.setBearerAuth(authenticationHelper.getUserToken()))
                    .build();
            return next.exchange(newRequest);
        }
        if(needServiceAccountToken){
            log.debug("Service account tokenValue is necessary to call URI :"+request.url());
            ClientRequest newRequest = ClientRequest.from(request)
                    .headers(h -> h.setBearerAuth(getServiceAccountToken()))
                    .build();
            return next.exchange(newRequest);
        }

        return next.exchange(request);
    }

    /**
     * Returns the current service account access tokenValue <b>for Rmes calls</b>, refreshing it if necessary.
     * <p>
     * This method checks whether the existing tokenValue is missing or close to expiration.
     * If the tokenValue has expired (or will expire within the configured margin), it automatically
     * retrieves a new one. Otherwise, it returns the cached tokenValue.
     * </p>
     * <p>
     * The expiration margin is defined in seconds and converted to milliseconds
     * to ensure the tokenValue is refreshed slightly before its actual expiration time.
     * </p>
     *
     * @return the valid service account access tokenValue as a String.
     * @throws IOException if an error occurs while retrieving a new tokenValue.
     */
    public synchronized String getServiceAccountToken()  {
        long marginMillis = serviceAccountProperties.refreshMargin() * 1000L;
        // We had a margin of 5 seconds for the expiration time
        if (serviceAccountToken == null
                || serviceAccountToken.isExpired(marginMillis)) {
            serviceAccountToken = retrieveNewServiceAccountToken(
                    serviceAccountProperties.authServerUrl(),
                    serviceAccountProperties.realm(),
                    serviceAccountProperties.clientId(),
                    serviceAccountProperties.clientSecret());
        }
        log.debug("ServiceAccountToken retrieved");
        return serviceAccountToken.tokenValue();
    }


    /**
     * ----------- Following code has been copy-paste from perret-api -----------
     * Retrieves a new access tokenValue for the application service account.
     * <p>
     * This method builds and sends a POST request to the configured authorization server's
     * tokenValue endpoint. The request includes the client ID, client secret, and requested scopes.
     * If the request is successful, the method extracts the {@code access_token} and its
     * expiration time ({@code expires_in}) from the response and updates the internal
     * {@code serviceAccountToken} and {@code tokenExpirationTime} fields.
     * </p>
     *
     * <p>
     * The resulting tokenValue is used to authenticate service-to-service requests.
     * If the tokenValue request fails (e.g., invalid credentials, server error, or malformed response),
     * an {@link TokenException} is thrown with details about the failure.
     * </p>
     *
     * @throws TokenException if the tokenValue cannot be retrieved due to a network error,
     *                     an invalid response format, or an unexpected HTTP status code.
     */
    private OidcToken retrieveNewServiceAccountToken(
            String authServerUrl, String realm, String clientId, String secretId) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format(
                "grant_type=client_credentials&client_id=%s&client_secret=%s&scope=openid profile roles",
                clientId, secretId);
        log.debug(
                "Calling auth endpoint {} with body {}",
                tokenUrl,
                secretId == null ? body : body.replace(secretId, "<SECRET>"));

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {});
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody == null
                        || !responseBody.containsKey(ACCESS_TOKEN)
                        || !(responseBody.get(ACCESS_TOKEN) instanceof String)) {
                    throw new TokenException("Invalid response: Missing or incorrect 'access_token'");
                }
                Integer expiresIn = (Integer) responseBody.get("expires_in");
                return new OidcToken(
                        (String) responseBody.get(ACCESS_TOKEN),
                        System.currentTimeMillis() + (expiresIn.longValue() * 1000L));
            } else {
                throw new TokenException("Failed to retrieve service account tokenValue, status: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            throw new TokenException(
                    "HTTP error while fetching tokenValue: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new TokenException("Unexpected error while fetching tokenValue: " + e.getMessage());
        }
    }

}