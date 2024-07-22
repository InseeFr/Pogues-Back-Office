package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.api.remote.eno.transforms.EnoClient;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api/healthcheck")
@Tag(name="1. Public Resources")
@Slf4j
public class HealthCheck {

    @Autowired
    EnoClient enoClient;

    @Autowired
    private QuestionnaireRepository questionnaireServiceQuery;

    @Autowired
    WebClient webClient;

    @Value("${application.metadata.ddi-as}")
    String ddiAccessServicesUrl;

    @Value("${application.stromae.orbeon.host}")
    String orbeonHost;

    @Value("${application.stromae.host}")
    String stromaeDbHost;

    @Value("${application.queen.vis.host}")
    String queenHost;

    @Value("${application.stromaev2.vis.host}")
    String stromaeV2Host;

    @Value("${application.stromaev3.vis.host}")
    String stromaeV3Host;

    @GetMapping("")
    @Operation(
            summary = "Perform HealthCheck on Pogues environment",
            description = "This method will return the status of applications needed for Pogues"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "209", description = "Warning"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<ObjectNode> getHealthcheck(@RequestParam(name = "legacy", defaultValue = "false") boolean legacy) {
        boolean haveError=false;
        AtomicBoolean haveWarning= new AtomicBoolean(false);
        log.debug("Begin healthCheck");

        ObjectNode finalResponse = JsonNodeFactory.instance.objectNode();

        try {
            enoClient.getParameters();
            finalResponse.put("Eno","OK");
        } catch (Exception e) {
            haveWarning.set(true);
            log.error(e.getMessage());
            finalResponse.put("Eno","KO, Eno API doesn't return default parameters");
        }
        try {
            questionnaireServiceQuery.countQuestionnaires();
            finalResponse.put("DB PostgreSql","OK");
        } catch (Exception e) {
            haveError=true;
            log.error(e.getMessage());
            finalResponse.put("DB PostgreSql","KO");
        }

        URI uriExist = UriComponentsBuilder.fromHttpUrl(stromaeDbHost).path("/exist/apps/dashboard/index.html").build().toUri();
        URI uriOrbeon = UriComponentsBuilder.fromHttpUrl(orbeonHost)
                .path("/rmesstromae/fr/esa-dc-2018/simpsons/new")
                .queryParam("unite-enquete","123456789")
                .build().toUri();
        URI uriQueen = UriComponentsBuilder.fromHttpUrl(queenHost).path("/index.html").build().toUri();
        // URI uriDDIAS = UriComponentsBuilder.fromHttpUrl(ddiAccessServicesUrl).path("/env").build().toUri();
        URI uriStromaeV2 = UriComponentsBuilder.fromHttpUrl(stromaeV2Host).path("/index.html").build().toUri();
        URI uriStromaeV3 = UriComponentsBuilder.fromHttpUrl(stromaeV3Host).path("/index.html").build().toUri();

        Map<String, URI> externalServices = new HashMap<>();

        if(legacy){
            externalServices.put("Exist", uriExist);
            externalServices.put("Orbeon", uriOrbeon);
        }
        externalServices.put("Queen", uriQueen);
        // externalServices.put("DDI - AS", uriDDIAS);
        externalServices.put("Stromae - V2", uriStromaeV2);
        externalServices.put("Stromae - V3", uriStromaeV3);

        externalServices.forEach((name, uri) -> {
            log.info("Check"+name+": "+uri);
            if(checkExternalService(uri)) finalResponse.put(name,"OK");
            else{
                haveWarning.set(true);
                finalResponse.put(name,"KO");
            }
        }
        );
        log.debug("HealthCheck complete");

        if (!haveError && !haveWarning.get()) {
            return ResponseEntity.status(HttpStatus.OK).body(finalResponse);
        } else if (haveWarning.get() && !haveError) {
            return ResponseEntity.status(209).body(finalResponse);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalResponse);
        }
    }

    private boolean checkExternalService(URI externalServiceUri) {
        try {
            webClient.get().uri(externalServiceUri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            log.error(String.format("%s for %s", e.getStatusCode(), externalServiceUri));
            return false;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

}
