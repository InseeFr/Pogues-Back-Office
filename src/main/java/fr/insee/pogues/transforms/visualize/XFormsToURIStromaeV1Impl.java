package fr.insee.pogues.transforms.visualize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
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
    public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
    	try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(serviceUriHost)
                    .path(serviceUriVisualizationPath)
                    .path("dataCollection").path("questionnaire").build().toUri();
            ResponseEntity<String> response = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(input)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
                return String.format("%s/%s%s",serviceUriOrbeonHost,"rmesstromae", response.getBody());
            }
            throw new Exception(String.format("%s:%s", getClass().getName(), response.getStatusCode()));
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
    }
}
