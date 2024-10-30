package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.exception.EnoException;
import fr.insee.pogues.webservice.model.StudyUnitEnum;
import fr.insee.pogues.webservice.rest.PoguesException;
import lombok.NonNull;
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
    private final WebClient webClient;

    private static final String DEFAULT_CONTEXT = "DEFAULT";
    private static final String BASE_PATH = "/questionnaire/" + DEFAULT_CONTEXT;

    private static final String MODE = "CAWI";

    public EnoClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String getDDI32ToDDI33 (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, "/questionnaire/ddi32-2-ddi33");
    }


    @Override
    public String getXMLPoguesToDDI (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, "/questionnaire/poguesxml-2-ddi");
    }


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
        String wsPath = buildWSPath(contextRequestParam, modePathParam); // Extraction de la construction du chemin

        queryParams.add("dsfr", Boolean.TRUE.equals(params.get("dsfr")) ? "true" : "false");
        return callEnoApiWithParams(inputAsString, wsPath, queryParams);
    }

    /**
     * Retrieves the context parameter from the provided map of parameters.
     * This method attempts to fetch the "context" parameter from the specified map
     * and casts it to a {@code StudyUnitEnum}. If the parameter is not present or is {@code null},
     * it returns a default value {@code StudyUnitEnum.DEFAULT}.
     * @param params a map of parameters where the "context" key may be present,
     *               associated with a {@code StudyUnitEnum} value.
     * @return the {@code StudyUnitEnum} value associated with the "context" key,
     *         or {@code StudyUnitEnum.DEFAULT} if not found or {@code null}.
     */
    static StudyUnitEnum getContextParam(Map<String, Object> params) {
        StudyUnitEnum context = (StudyUnitEnum) params.get("context");
        return (context != null) ? context : StudyUnitEnum.DEFAULT;
    }

    /**
     * Constructs the WSPath for the questionnaire based on the context and mode.
     *
     * @param context The context of type {@link StudyUnitEnum} used in the path. Cannot be null.
     * @param mode The specified mode for the path. Cannot be null.
     * @return The WSPath as a string, structured as "questionnaire/{context}/lunatic-json/{mode}".
     * @throws NullPointerException if {@code context} or {@code mode} is null.
     */
    static String buildWSPath(@NonNull StudyUnitEnum context, @NonNull String mode) {
        return "questionnaire/" + context + "/lunatic-json/" + mode;
    }

    @Override
    public String getDDITOXForms(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, BASE_PATH+"/xforms");
    }

    @Override
    public String getDDIToODT (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, BASE_PATH+"/fodt");
    }


    @Override
    public void getParameters () {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(enoHost)
                .path("/parameters/xml/all")
                .build().toUri();

        String xmlParams = webClient.get()
                .uri(uri)
                .accept(MediaType.ALL)
                .retrieve().bodyToMono(String.class).block();

        log.debug(xmlParams);
    }

    private String callEnoApi(String inputAsString, String wsPath) throws EnoException, PoguesException {
        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        return callEnoApiWithParams(inputAsString, wsPath, queryParams);
    }

    private String callEnoApiWithParams(String inputAsString, String wsPath, MultiValueMap<String,String> params) throws EnoException, PoguesException {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(enoHost)
                .path(wsPath)
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

