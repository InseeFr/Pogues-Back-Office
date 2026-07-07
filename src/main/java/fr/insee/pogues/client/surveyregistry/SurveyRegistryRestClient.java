package fr.insee.pogues.client.surveyregistry;

import fr.insee.pogues.client.surveyregistry.exceptions.NomenclatureNotFoundException;
import fr.insee.pogues.client.surveyregistry.exceptions.SurveyRegistryException;
import fr.insee.pogues.client.surveyregistry.mapper.SurveyRegistryMapper;
import fr.insee.pogues.client.surveyregistry.model.CodesListMetadataDto;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Client which allow to access registry data
 * through REST endpoints calls towards Registry API.
 *
 * @since 5.0
 */
@Slf4j
@Service
public class SurveyRegistryRestClient implements SurveyRegistryClient {

    public static final String SEARCH_CONFIGURATION_EXPAND_PARAM = "SEARCH_CONFIGURATION";

    private final RestClient restClient;

    public SurveyRegistryRestClient(@Qualifier("surveyRegistryApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Fetch the nomenclatures from the registry.
     * Only return ones that are valid and non-deprecated
     * (i.e. can be used by the user).
     */
    @Override
    public List<NomenclatureDTO> getNomenclatures() throws HttpClientErrorException, HttpServerErrorException {
        URI uri = UriComponentsBuilder
                .fromPath("codes-lists")
                .queryParam("expand", SEARCH_CONFIGURATION_EXPAND_PARAM)
                .queryParam("valid", true)
                .queryParam("deprecated", false)
                .encode()
                .build()
                .toUri();
        List<CodesListMetadataDto> codesListsMetadata = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (codesListsMetadata == null) return List.of();

        return codesListsMetadata.stream()
                .map(SurveyRegistryMapper::toDTO)
                .toList();
    }
    @Override
    public NomenclatureDTO getNomenclatureMetadataById(UUID nomenclatureId) {
        URI uri = UriComponentsBuilder
                .fromPath("codes-lists/{nomenclatureId}/metadata")
                .queryParam("expand", SEARCH_CONFIGURATION_EXPAND_PARAM)
                .buildAndExpand(nomenclatureId)
                .encode()
                .toUri();

        log.info("Get codelist uri: {}", uri);

        CodesListMetadataDto codesListsMetadata = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            if (response.getStatusCode().value() == 404) {
                                throw new NomenclatureNotFoundException(
                                        "Nomenclature " + nomenclatureId + " not found");
                            }
                            throw new SurveyRegistryException("Error when getting nomenclature: " + response.getStatusCode());
                        })

                .body(new ParameterizedTypeReference<>() {});

        if(codesListsMetadata == null)
            throw new NomenclatureNotFoundException("Nomenclature " + nomenclatureId + " not found");

        return SurveyRegistryMapper.toDTO(codesListsMetadata);
    }
}
