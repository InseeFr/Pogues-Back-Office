package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.exception.EnoException;
import fr.insee.pogues.model.EnoContext;
import fr.insee.pogues.exception.PoguesException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Implementation of EnoClient using http.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnoHttpClient implements EnoClient {

    @Value("${application.eno.host}")
    String enoHost;

    private final WebClient webClient;

    private static final String DEFAULT_CONTEXT = "DEFAULT";
    private static final String DEFAULT_CONTEXT_PATH = "/questionnaire/" + DEFAULT_CONTEXT;
    private static final String DDI_FILE_NAME = "ddi.xml";
    private static final String POGUES_XML_FILE_NAME = "pogues.xml";
    private static final String POGUES_JSON_FILE_NAME = "pogues.json";
    private static final String DSFR_QUERY_PARAM = "dsfr";


    /** {@link EnoClient#getParameters()} */
    @Override
    public void getParameters() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(enoHost)
                .path("/parameters/xml/all")
                .build().toUri();
        String xmlParams = webClient.get()
                .uri(uri)
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(String.class).block();
        log.debug("Xml parameters received from the Eno external API:{}{}", System.lineSeparator(), xmlParams);
    }

    @Override
    public String getPoguesXmlToDDI(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString,  POGUES_XML_FILE_NAME, "/questionnaire/poguesxml-2-ddi");
    }

    @Override
    public String getDDIToODT (String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, DDI_FILE_NAME, DEFAULT_CONTEXT_PATH +"/fodt");
    }

    @Override
    public String getDDIToFO(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, DDI_FILE_NAME, DEFAULT_CONTEXT_PATH +"/fo");
    }

    @Override
    public String getDDIToXForms(String inputAsString) throws EnoException, PoguesException {
        return callEnoApi(inputAsString, DDI_FILE_NAME, DEFAULT_CONTEXT_PATH +"/xforms");
    }

    /**
     * @deprecated {@link EnoClient#getDDIToLunaticJSON(String, Map)}
     */
    @Override
    @Deprecated(since = "4.9.2")
    public String getDDIToLunaticJSON(String inputAsString, Map<String, Object> params) throws EnoException, PoguesException {
        log.info("getDDITOLunaticJSON - started");

        EnoContext context = getContextParam(params);
        String mode = getModeParam(params);
        String wsPath = "questionnaire/" + context + "/lunatic-json/" + mode;

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(DSFR_QUERY_PARAM, getDsfrParam(params));

        return callEnoApiWithParams(inputAsString, DDI_FILE_NAME, wsPath, queryParams);
    }

    @Override
    public String getPoguesJsonToLunaticJson(String inputAsString, Map<String, Object> params) throws EnoException, PoguesException {
        log.info("getJSONPoguesToLunaticJson - started");

        EnoContext context = getContextParam(params);
        String mode = getModeParam(params);
        String wsPath = "/questionnaire/pogues-2-lunatic/" + context + "/" + mode;

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(DSFR_QUERY_PARAM, getDsfrParam(params));

        return callEnoApiWithParams(inputAsString, POGUES_JSON_FILE_NAME, wsPath, queryParams);
    }

    /** Returns the Eno context from the params map. Default value is the 'DEFAULT' context. */
    static EnoContext getContextParam(Map<String, Object> params) {
        EnoContext enoContext = (EnoContext) params.get("context");
        if (enoContext == null)
            log.warn("null context sent in a Eno request.");
        return (enoContext != null) ? enoContext : EnoContext.DEFAULT;
    }

    static String getModeParam(Map<String, Object> params) {
        Object modePathParam = params.get("mode");
        if (modePathParam == null)
            throw new IllegalStateException("No 'mode' defined in params.");
        return modePathParam.toString();
    }

    static String getDsfrParam(Map<String, Object> params) {
        return Boolean.TRUE.equals(params.get("dsfr")) ? "true" : "false";
    }

    private String callEnoApi(String inputAsString, String fileName, String wsPath) throws EnoException, PoguesException {
        MultiValueMap<String,String> emptyParams = new LinkedMultiValueMap<>();
        return callEnoApiWithParams(inputAsString, fileName, wsPath, emptyParams);
    }

    private String callEnoApiWithParams(String inputAsString, String fileName, String wsPath, MultiValueMap<String,String> params)
            throws EnoException, PoguesException {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(enoHost)
                .path(wsPath)
                .queryParams(params)
                .build().toUri();

        log.info("Call Eno API with URI: {}", uri);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("in", new ByteArrayResourceWithFileName(
                fileName, inputAsString.getBytes(StandardCharsets.UTF_8)));

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
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PoguesException(500, "Unknown error during generation", "");
        }
    }

}
