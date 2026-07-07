package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureUrlDTO;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import fr.insee.pogues.service.stub.SurveyRegistryClientStub;
import fr.insee.pogues.utils.PoguesSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;

import java.util.List;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NomenclatureServiceTest {

    @Mock
    VersionService versionService;

    @Mock
    SuggesterVisuService suggesterVisuService;

    private NomenclatureService nomenclatureService;
    private QuestionnaireServiceStub questionnaireService;
    private SurveyRegistryClientStub surveyRegistryClient;

    @BeforeEach
    void init() {
        questionnaireService = new QuestionnaireServiceStub();
        surveyRegistryClient = new SurveyRegistryClientStub();
        nomenclatureService = new NomenclatureService(questionnaireService, versionService, suggesterVisuService, surveyRegistryClient);
    }

    @Test
    @DisplayName("Should get nomenclature urls from a questionnaire Id")
    void getNomenclaturesUrls_fromQuestionnaireId() throws Exception {
        List<NomenclatureUrlDTO> expected = List.of(new NomenclatureUrlDTO("L_PAYS", "https://registry/codes-lists/L_PAYS"));
        Mockito.when(suggesterVisuService.computeNomenclaturesUrls("my-q-id")).thenReturn(expected);

        List<NomenclatureUrlDTO> result = nomenclatureService.getNomenclaturesUrls("my-q-id");

        assertThat(result).isEqualTo(expected);
        Mockito.verify(suggesterVisuService).computeNomenclaturesUrls("my-q-id");
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
    @DisplayName("Should get all Nomenclatures from")
    void getAllNomenclatures_success(){
        // Given a survey registry with 1 nomenclature
        surveyRegistryClient.setNomenclatures(List.of(new NomenclatureDTO()));

        // When we get the survey registry's nomenclatures
        List<NomenclatureDTO> nomenclatures = nomenclatureService.getAllNomenclatures();

        // Then the nomenclatures are fetched
        assertThat(nomenclatures).hasSize(1);
    }
}
