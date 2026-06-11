package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import fr.insee.pogues.utils.PoguesSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;

import java.util.List;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class NomenclatureServiceTest {

    @Mock
    VersionService versionService;

    private NomenclatureService nomenclatureService;
    private QuestionnaireServiceStub questionnaireService;

    @BeforeEach
    void init() {
        questionnaireService = new QuestionnaireServiceStub();
        nomenclatureService = new NomenclatureService(questionnaireService, versionService);
        initMocks(this);
    }

    @Test
    @DisplayName("Should fetch questionnaire nomenclatures")
    void getQuestionnaireNomenclatures_success() throws Exception {
        // Given a questionnaire with 29 nomenclatures
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/withAllNomenclatures.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's nomenclatures
        List<ExtendedNomenclatureDTO> nomenclatures = nomenclatureService.getQuestionnaireNomenclatures("mawgv66f");

        // Then the nomenclatures are fetched
        assertThat(nomenclatures).hasSize(29);
        assertThat(nomenclatures.getFirst().getRelatedQuestionNames())
                .containsExactly("N_1");
    }

    @Test
    @DisplayName("Should mock all Nomenclatures from resources")
    void getAllNomenclatures_success(){
        List<NomenclatureDTO> nomenclatures = nomenclatureService.getAllNomenclatures();

        assertThat(nomenclatures).hasSize(34);

        NomenclatureDTO activites = nomenclatures.stream().filter(n->"L_ACTIVITES-2-2-0".equals(n.getId())).findFirst().get();
        assertEquals("urn:ddi:fr.insee:l_activites-2-2-0:1", activites.getUrn());
        assertNotNull(activites.getSuggesterParameters());
    }
}
