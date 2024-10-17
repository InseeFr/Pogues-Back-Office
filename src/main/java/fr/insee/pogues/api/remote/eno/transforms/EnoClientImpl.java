package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.exception.EnoException;
import fr.insee.pogues.webservice.model.StudyUnitEnum;
import fr.insee.pogues.webservice.rest.PoguesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class EnoClientImpl implements EnoClient{

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
        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        String modePathParam = params.get("mode") != null ? params.get("mode").toString() : MODE;
        StudyUnitEnum contextRequestParam = getContextParam(params); // Extraction de cette logique dans une méthode séparée
        String WSPath = buildWSPath(contextRequestParam, modePathParam); // Extraction de la construction du chemin

        queryParams.add("dsfr", Boolean.TRUE.equals(params.get("dsfr")) ? "true" : "false");
        return callEnoApiWithParams(inputAsString, WSPath, queryParams);
    }

    // Méthode pour récupérer le contexte
    public StudyUnitEnum getContextParam(Map<String, Object> params) {
        return (StudyUnitEnum) params.getOrDefault("context", StudyUnitEnum.DEFAULT);
    }

    // Méthode pour construire le WSPath
    public String buildWSPath(StudyUnitEnum context, String mode) {
        return "questionnaire/" + context + "/lunatic-json/" + mode;
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
                .fromHttpUrl(enoHost)
                .path("/parameters/xml/all")
                .build().toUri();

        String xmlParams = webClient.get()
                .uri(uri)
                .accept(MediaType.ALL)
                .retrieve().bodyToMono(String.class).block();

        log.debug(xmlParams);
    };

    private String callEnoApi(String inputAsString, String WSPath) throws EnoException, PoguesException {
        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        return callEnoApiWithParams(inputAsString, WSPath, queryParams);
    }

    private String callEnoApiWithParams(String inputAsString, String WSPath, MultiValueMap<String,String> params) throws EnoException, PoguesException {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(enoHost)
                .path(WSPath)
                .queryParams(params)
                .build().toUri();

        log.info("Call Eno with URI : {}", uri);

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

