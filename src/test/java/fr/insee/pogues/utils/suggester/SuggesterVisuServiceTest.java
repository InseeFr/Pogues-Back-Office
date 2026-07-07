package fr.insee.pogues.utils.suggester;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureUrlDTO;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.service.SuggesterVisuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.JsonNode;

import java.util.List;

import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SuggesterVisuServiceTest {

    @Mock
    IQuestionnaireService questionnaireService;

    SuggesterVisuService suggesterVisuService;

    static final String REGISTRY_HOST = "https://registry.example.com";

    @BeforeEach
    void init() {
        suggesterVisuService = new SuggesterVisuService(questionnaireService);
        ReflectionTestUtils.setField(suggesterVisuService, "surveyRegistryApi", REGISTRY_HOST);
    }

    @Test
    @DisplayName("should build nomenclature URLs from their id, using registry")
    void getNomenclatureUrls_fromIds() {
        List<NomenclatureUrlDTO> result = suggesterVisuService.computeNomenclaturesUrls(List.of("L_PAYS", "L_DEPNAIS"));

        assertThat(result).containsExactly(
                new NomenclatureUrlDTO("L_PAYS", REGISTRY_HOST + "/codes-lists/L_PAYS"),
                new NomenclatureUrlDTO("L_DEPNAIS", REGISTRY_HOST + "/codes-lists/L_DEPNAIS")
        );
    }

    @Test
    @DisplayName("should return URLs for each nomenclature, from a questionnaireId")
    void getNomenclatureUrls_fromQuestionnaireId() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/withAllNomenclatures.json");
        Mockito.when(questionnaireService.getQuestionnaireModelByIDWithReferences("mawgv66f")).thenReturn(questionnaire);

        List<NomenclatureUrlDTO> result = suggesterVisuService.computeNomenclaturesUrls("mawgv66f");

        assertThat(result).hasSize(29);
        assertThat(result).allSatisfy(nomenclature ->
                assertThat(nomenclature.url()).isEqualTo(REGISTRY_HOST + "/codes-lists/" + nomenclature.id())
        );
    }

    @Test
    @DisplayName("createJsonNomenclaturesForVisu should return a JsonNode with id->url entries")
    void createJsonNomenclaturesForVisu() {
        JsonNode result = suggesterVisuService.createJsonNomenclaturesForVisu(List.of("L_PAYS", "L_DEPNAIS"));

        assertThat(result.get("L_PAYS").asString()).isEqualTo(REGISTRY_HOST + "/codes-lists/L_PAYS");
        assertThat(result.get("L_DEPNAIS").asString()).isEqualTo(REGISTRY_HOST + "/codes-lists/L_DEPNAIS");
    }
}
