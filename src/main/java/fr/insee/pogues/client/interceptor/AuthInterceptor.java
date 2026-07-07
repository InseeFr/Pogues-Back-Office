package fr.insee.pogues.client.interceptor;

import fr.insee.pogues.configuration.auth.user.AuthenticationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/** This interceptor will add the user token to http requests. */
@Slf4j
public class AuthInterceptor implements ClientHttpRequestInterceptor {

    private final AuthenticationHelper authenticationHelper;

    public AuthInterceptor(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String jwt = authenticationHelper.getUserToken();
        if (jwt != null) request.getHeaders().setBearerAuth(jwt);

        return execution.execute(request, body);
    }
}
