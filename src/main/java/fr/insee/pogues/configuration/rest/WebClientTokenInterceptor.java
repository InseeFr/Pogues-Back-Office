package fr.insee.pogues.configuration.rest;

import fr.insee.pogues.configuration.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebClientTokenInterceptor implements ExchangeFilterFunction {

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        boolean needToken = applicationProperties.externalSecureUrls().stream()
                .filter(Predicate.not(String::isBlank)) // remove empty secured urls
                .filter(secureUrl -> request.url().toString().contains(secureUrl))
                .count() > 0;

        if(!needToken) return next.exchange(request);

        log.debug("Token is necessary to call URI :"+request.url());
        String jwt = authenticationHelper.getUserToken();
        ClientRequest newRequest = ClientRequest.from(request)
                .headers(h -> h.setBearerAuth(jwt))
                .build();
        return next.exchange(newRequest);
    }
}