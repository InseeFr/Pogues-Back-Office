package fr.insee.pogues.configuration.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class WebClientTokenInterceptor implements ExchangeFilterFunction {

    private final AuthenticationHelper authenticationHelper;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String jwt = authenticationHelper.getUserToken();
        ClientRequest newRequest = ClientRequest.from(request)
                .headers(h -> h.setBearerAuth(jwt))
                .build();
        return next.exchange(newRequest);
    }
}