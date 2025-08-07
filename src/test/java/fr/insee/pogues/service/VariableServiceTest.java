package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.VariableNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.webservice.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VariableServiceTest {

    @Mock
    VersionService versionService;

    private VariableService variableService;
    private QuestionnaireServiceStub questionnaireService;

    @BeforeEach
    void init() {
        questionnaireService = new QuestionnaireServiceStub();
        variableService = new VariableService(questionnaireService, versionService);
    }

    @Test
    @DisplayName("Should update questionnaire variable")
    void upsertQuestionnaireVariable_success_updated() throws Exception {
        // Given a questionnaire with 1 variable
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());

        // When we update the variable
        VariableType variable = new CollectedVariableType();
        variable.setId("lmyo22nw");
        boolean isCreated = variableService.upsertQuestionnaireVariable("lmyoceix", variable);

        // Then the questionnaire is updated
        assertFalse(isCreated);
        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());
    }

    @Test
    @DisplayName("Should create questionnaire variable")
    void upsertQuestionnaireVariable_success_created() throws Exception {
        // Given a questionnaire with 1 variable
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());

        // When we insert a new variable
        VariableType variable = new CollectedVariableType();
        variable.setId("new-variable");
        boolean isCreated = variableService.upsertQuestionnaireVariable("lmyoceix", variable);

        // Then the questionnaire is created
        assertTrue(isCreated);
        assertEquals(2, variableService.getQuestionnaireVariables("lmyoceix").size());
    }

    @Test
    @DisplayName("Should trigger an error when we try to add a variable in a questionnaire that does not exist")
    void upsertQuestionnaireVariable_error_questionnaireNotFound() {
        // Given a questionnaire that does not exist

        // When we insert a new variable
        VariableType variable = new CollectedVariableType();
        variable.setId("new-variable");
        PoguesException exception = assertThrows(
                PoguesException.class,
                () -> variableService.upsertQuestionnaireVariable("no-questionnaire", variable));

        // Then a 404 exception is thrown
        assertEquals(404, exception.getStatus());
        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("Should delete questionnaire variable")
    void deleteQuestionnaireVariable_success() throws Exception {
        // Given a questionnaire with 1 variable
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());

        // When we delete the variable
        assertDoesNotThrow(() -> variableService.deleteQuestionnaireVariable("lmyoceix", "lmyo22nw"));

        // Then there is 0 variables in the questionnaire
        assertEquals(0, variableService.getQuestionnaireVariables("lmyoceix").size());
    }

    @Test
    @DisplayName("Should trigger an error when we try to delete a variable that does not exist")
    void deleteQuestionnaireVariable_error_variableNotFound() throws Exception {
        // Given a questionnaire with 1 variable
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());

        // When we delete a variable that does not exist
        VariableNotFoundException exception = assertThrows(
                VariableNotFoundException.class,
                () -> variableService.deleteQuestionnaireVariable("lmyoceix", "no-variable"));

        // Then a 404 exception is thrown and no variable has been deleted
        assertEquals(404, exception.getStatus());
        assertEquals(ErrorCode.VARIABLE_NOT_FOUND, exception.getErrorCode());
        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());
    }

    @Test
    @DisplayName("Should trigger an error when we try to delete a variable in a questionnaire that does not exist")
    void deleteQuestionnaireVariable_error_questionnaireNotFound() {
        // Given a questionnaire that does not exist

        // When we delete a variable
        PoguesException exception = assertThrows(
                PoguesException.class,
                () -> variableService.deleteQuestionnaireVariable("no-questionnaire", "no-variable"));

        // Then a 404 exception is thrown
        assertEquals(404, exception.getStatus());
        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("Should fetch questionnaire variables")
    void getQuestionnaireVariables_success() throws Exception {
        // Given a questionnaire with 1 variable
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        VariableType expected = new CollectedVariableType();
        expected.setId("lmyo22nw");
        expected.setName("Q1");
        expected.setLabel("Q1 label");

        // When we get the questionnaire's variables
        List<VariableType> res = variableService.getQuestionnaireVariables("lmyoceix");

        // Then the variable is fetched
        assertEquals(1, res.size());
        assertEquals(expected.getId(), res.getFirst().getId());
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch variables from a questionnaire that does not exist")
    void getQuestionnaireVariables_error_questionnaireNotFound() {
        // Given a questionnaire that does not exist

        // When we get the questionnaire's variables
        PoguesException exception = assertThrows(
                PoguesException.class,
                () -> variableService.getQuestionnaireVariables("no-questionnaire"));

        // Then a 404 exception is thrown
        assertEquals(404, exception.getStatus());
        assertNull(exception.getErrorCode());
    }

}
