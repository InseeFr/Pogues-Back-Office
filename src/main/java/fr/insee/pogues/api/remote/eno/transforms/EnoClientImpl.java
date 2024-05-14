package fr.insee.pogues.api.remote.eno.transforms;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import fr.insee.pogues.exception.EnoException;
import fr.insee.pogues.webservice.rest.PoguesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class EnoClientImpl implements EnoClient{

    @Value("${application.eno.scheme}")
    String enoScheme;
    @Value("${application.eno.host}")
    String enoHost;

    @Autowired
    private WebClient webClient;

    private static final String BASE_PATH = "/questionnaire/DEFAULT";
    private static final String MODE = "CAWI";

    public EnoClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String getDDI32ToDDI33 (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, "/questionnaire/ddi32-2-ddi33");
    };


    @Override
    public String getXMLPoguesToDDI (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, "/questionnaire/poguesxml-2-ddi");
    };


    @Override
    public String getDDIToFO(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, BASE_PATH+"/fo");
    }

    @Override
    public String getDDITOLunaticXML(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, BASE_PATH+"/lunatic-xml");
    }

    @Override
    public String getDDITOLunaticJSON(String inputAsString, Map<String, Object> params) throws EnoException, PoguesException {
        String WSPath;

        if (params.get("mode") != null) {
            WSPath = BASE_PATH+"/lunatic-json/"+params.get("mode").toString();
            log.info("Url for DDI to Lunatic transformation : "+WSPath);
        } else {
            WSPath = BASE_PATH+"/lunatic-json/"+MODE;
        }
        return callEnoApi(inputAsString, WSPath);
    }

    @Override
    public String getDDITOXForms(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, BASE_PATH+"/xforms");
    }

    @Override
    public String getDDIToODT (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, BASE_PATH+"/fodt");
    };


    @Override
    public void getParameters () throws Exception{
        URI uri = UriComponentsBuilder
                .fromHttpUrl(String.format("%s://%s", enoScheme, enoHost))
                .path("/parameters/xml/all")
                .build().toUri();

        String xmlParams = webClient.get()
                .uri(uri)
                .accept(MediaType.ALL)
                .retrieve().bodyToMono(String.class).block();

        log.debug(xmlParams);
    };

    private String callEnoApi(String inputAsString, String WSPath) throws EnoException, PoguesException {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(String.format("%s://%s", enoScheme, enoHost))
                .path(WSPath)
                .build().toUri();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("in",
                new ByteArrayResourceWithFileName(
                        "form.xml",
                        inputAsString.getBytes(StandardCharsets.UTF_8))
        );

        try {
            return webClient.post()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new EnoException(e.getResponseBodyAsString(), null);
        } catch (Exception e){
            throw new PoguesException(500, "Unknow error during generation", "");
        }
    }

}

