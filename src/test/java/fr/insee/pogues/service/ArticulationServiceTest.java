package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.controller.error.ErrorCode;
import fr.insee.pogues.exception.*;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import fr.insee.pogues.utils.PoguesSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArticulationServiceTest {

    @Mock
    VersionService versionService;

    private ArticulationService articulationService;
    private QuestionnaireServiceStub questionnaireService;

    @BeforeEach
    void init() {
        questionnaireService = new QuestionnaireServiceStub();
        articulationService = new ArticulationService(questionnaireService, versionService);
    }

    @Test
    @DisplayName("Should fetch questionnaire articulation")
    void getQuestionnaireArticulation_success() throws Exception {
        // Given a questionnaire in VTL with a roundabout and an articulation
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/articulation.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        Articulation expected = new Articulation();
        Item item = new Item();
        item.setLabel("my articulation label");
        item.setValue("my articulation value");
        item.setType(ValueTypeEnum.VTL);
        expected.getItems().add(item);

        // When we get the questionnaire's articulation
        Articulation res = articulationService.getQuestionnaireArticulation("m7d5k0hy");

        // Then the articulation is fetched
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return null when there is no articulations")
    void getQuestionnaireArticulation_success_null() throws Exception {
        // Given a questionnaire in VTL with a roundabout and no articulations
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundabout.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's articulation
        Articulation res = articulationService.getQuestionnaireArticulation("m7d5k0hy");

        // Then the result is null
        assertNull(res);
    }

    @Test
    @DisplayName("Should throw an exception when there is no questionnaires related to the id")
    void getQuestionnaireArticulation_error_notFound() throws Exception {
        // Given a questionnaire that does not exist

        // When we get the questionnaire's articulation
        PoguesException exception = assertThrows(
                QuestionnaireNotFoundException.class,
                () -> articulationService.getQuestionnaireArticulation("q-id"));

        // Then an exception is thrown because this questionnaire does not exist
        assertEquals(404, exception.getStatus());
        assertEquals(ErrorCode.QUESTIONNAIRE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw an exception when not in VTL")
    void getQuestionnaireArticulation_error_notVTL() throws Exception {
        // Given a questionnaire not in VTL with a roundabout
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundaboutXPath.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's articulation
        PoguesException exception = assertThrows(
                QuestionnaireFormulaLanguageNotVTLException.class,
                () -> articulationService.getQuestionnaireArticulation("m7d5k0hy"));

        // Then an exception is thrown because this feature is not available
        assertEquals(422, exception.getStatus());
        assertEquals(ErrorCode.QUESTIONNAIRE_FORMULA_LANGUAGE_NOT_VTL, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw an exception when there is no roundabout")
    void getQuestionnaireArticulation_error_noRoundabout() throws Exception {
        // Given a questionnaire in VTL without a roundabout
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's articulation
        PoguesException exception = assertThrows(
                QuestionnaireRoundaboutNotFoundException.class,
                () -> articulationService.getQuestionnaireArticulation("lmyoceix"));

        // Then an exception is thrown because this feature is not available
        assertEquals(422, exception.getStatus());
        assertEquals(ErrorCode.QUESTIONNAIRE_ROUNDABOUT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should fetch questionnaire variables available for the articulation")
    void getQuestionnaireArticulationVariables_success() throws Exception {
        // Given a questionnaire in VTL with a roundabout
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundabout.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's variables available for the articulation
        List<VariableType> res = articulationService.getQuestionnaireArticulationVariables("m7d5k0hy");

        // Then the variables related to the roundabout are fetched
        assertEquals(5, res.size());
    }

    @Test
    @DisplayName("Should update questionnaire articulation")
    void upsertQuestionnaireArticulation_success_updated() throws Exception {
        // Given a questionnaire in VTL with a roundabout and an articulation
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/articulation.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        Articulation expected = new Articulation();
        Item item = new Item();
        item.setLabel("my articulation label");
        item.setValue("my articulation value");
        item.setType(ValueTypeEnum.VTL);
        expected.getItems().add(item);
        assertThat(articulationService.getQuestionnaireArticulation("m7d5k0hy")).usingRecursiveComparison().isEqualTo(expected);

        // When we update the questionnaire's articulation
        Articulation newArticulation = new Articulation();
        Item newItem = new Item();
        newItem.setLabel("my new articulation label");
        newItem.setValue("my new articulation value");
        newItem.setType(ValueTypeEnum.VTL);
        newArticulation.getItems().add(newItem);
        boolean isCreated = articulationService.upsertQuestionnaireArticulation("m7d5k0hy", newArticulation);

        // Then the articulation is updated
        assertFalse(isCreated);
        assertThat(articulationService.getQuestionnaireArticulation("m7d5k0hy")).usingRecursiveComparison().isEqualTo(newArticulation);
    }

    @Test
    @DisplayName("Should create questionnaire articulation")
    void upsertQuestionnaireArticulation_success_created() throws Exception {
        // Given a questionnaire in VTL with a roundabout and an articulation
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundabout.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        assertNull(articulationService.getQuestionnaireArticulation("m7d5k0hy"));

        // When we update the questionnaire's articulation
        Articulation newArticulation = new Articulation();
        Item newItem = new Item();
        newItem.setLabel("my new articulation label");
        newItem.setValue("my new articulation value");
        newItem.setType(ValueTypeEnum.VTL);
        newArticulation.getItems().add(newItem);
        boolean isCreated = articulationService.upsertQuestionnaireArticulation("m7d5k0hy", newArticulation);

        // Then the articulation is updated
        assertTrue(isCreated);
        assertThat(articulationService.getQuestionnaireArticulation("m7d5k0hy")).usingRecursiveComparison().isEqualTo(newArticulation);
    }

    @Test
    @DisplayName("Should delete questionnaire articulation")
    void deleteQuestionnaireArticulation_success() throws Exception {
        // Given a questionnaire in VTL with a roundabout and an articulation
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/articulation.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        assertNotNull(articulationService.getQuestionnaireArticulation("m7d5k0hy"));

        // When we delete the articulation
        assertDoesNotThrow(() -> articulationService.deleteQuestionnaireArticulation("m7d5k0hy"));

        // Then the articulation is deleted
        assertNull(articulationService.getQuestionnaireArticulation("m7d5k0hy"));
    }

}
