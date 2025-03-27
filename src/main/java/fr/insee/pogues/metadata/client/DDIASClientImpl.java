package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.exception.PoguesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class DDIASClientImpl implements DDIASClient {

    private static final String UNITS_PATH = "/meta-data/units";

    @Autowired
    private WebClient webClient;

    @Value("${application.metadata.ddi-as}")
    String ddiAsHost;

    @Override
    public List<Unit> getUnits() throws Exception {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(ddiAsHost)
                .path(UNITS_PATH)
                .build().toUri();
        log.info("Call DDIAS with URI : {}", uri);
        try {
            Unit[] units = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Unit[].class)
                    .block();
            return List.of(units);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new PoguesException(e.getStatusCode().value(), e.getStatusText(), e.getResponseBodyAsString());
        } catch (Exception e){
            throw new PoguesException(500, "Unknow error during metadata call", "");
        }
    }
}
