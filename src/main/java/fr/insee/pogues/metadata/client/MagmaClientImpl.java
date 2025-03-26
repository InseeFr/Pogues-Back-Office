package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
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
public class MagmaClientImpl implements MagmaClient {

    private static final String SERIES_PATH = "/operations/series";
    private static final String OPERATIONS_SERIE_PATH = "/operations/serie";
    private static final String OPERATIONS_PATH = "operations";
    private static final String SINGLE_OPERATION_PATH = "/operations/operation";

    @Autowired
    private WebClient webClient;

    @Value("${application.metadata.magma}")
    String magmaHost;

    @Override
    public List<Serie> getSeries() throws Exception {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(magmaHost)
                .path(SERIES_PATH)
                .queryParam("survey",true)
                .build().toUri();
        log.info("Call Magma with URI : {}", uri);
        try {
            Serie[] series = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Serie[].class)
                    .block();
            return List.of(series);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new PoguesException(e.getStatusCode().value(), e.getStatusText(), e.getResponseBodyAsString());
        } catch (Exception e){
            log.error(e.getMessage());
            throw new PoguesException(500, "Unknow error during metadata call", "");
        }
    }

    @Override
    public Serie getSerieById(String id) throws Exception {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(magmaHost)
                .path(OPERATIONS_SERIE_PATH)
                .pathSegment(id)
                .build().toUri();
        log.info("Call Magma with URI : {}", uri);
        try {
            Serie serie = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Serie.class)
                    .block();
            return serie;
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new PoguesException(e.getStatusCode().value(), e.getStatusText(), e.getResponseBodyAsString());
        } catch (Exception e){
            throw new PoguesException(500, "Unknow error during metadata call", "");
        }
    }

    @Override
    public List<Operation> getOperationsByIdSerie(String id) throws Exception {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(magmaHost)
                .path(OPERATIONS_SERIE_PATH)
                .pathSegment(id)
                .pathSegment(OPERATIONS_PATH)
                .build().toUri();
        log.info("Call Magma with URI : {}", uri);
        try {
            Operation[] operations = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Operation[].class)
                    .block();
            return List.of(operations);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new PoguesException(e.getStatusCode().value(), e.getStatusText(), e.getResponseBodyAsString());
        } catch (Exception e){
            throw new PoguesException(500, "Unknow error during metadata call", "");
        }
    }

    @Override
    public Operation getOperationById(String idOperation) throws Exception {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(magmaHost)
                .path(SINGLE_OPERATION_PATH)
                .pathSegment(idOperation)
                .build().toUri();
        log.info("Call Magma with URI : {}", uri);
        try {
            Operation operation = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Operation.class)
                    .block();
            return operation;
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new PoguesException(e.getStatusCode().value(), e.getStatusText(), e.getResponseBodyAsString());
        } catch (Exception e){
            throw new PoguesException(500, "Unknow error during metadata call", "");
        }
    }


}
