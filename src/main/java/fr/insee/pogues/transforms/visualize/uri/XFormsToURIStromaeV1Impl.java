package fr.insee.pogues.transforms.visualize.uri;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

@Service
@Slf4j
public class XFormsToURIStromaeV1Impl implements XFormsToURIStromaeV1 {
    
    @Autowired
    WebClient webClient;
    
    @Value("${application.stromae.host}")
    private String serviceUriHost;
    
    @Value("${application.stromae.orbeon.host}")
    private String serviceUriOrbeonHost;
    
    @Value("${application.stromae.vis.path}")
    private String serviceUriVisualizationPath;

    @Override
    public URI transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(serviceUriHost)
                    .pathSegment(serviceUriVisualizationPath)
                    .pathSegment((String) params.get("dataCollection"))
                    .pathSegment((String) params.get("questionnaire")).build().toUri();

            log.debug("Calling Exist-Db with URI "+uri);
            ResponseEntity<String> response = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(input.readAllBytes())
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
                return URI.create(String.format("%s/%s%s",serviceUriOrbeonHost,"rmesstromae", response.getBody()));
            }
            throw new Exception(String.format("%s:%s", getClass().getName(), response.getStatusCode()));
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
    }
}
