package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.VariableNotFoundException;
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
    void deleteQuestionnaireVariable_error_notFound() throws Exception {
        // Given a questionnaire with 1 variable
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("simple-questionnaire.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());

        // When we delete a variable that does not exist
        assertThrows(VariableNotFoundException.class, () -> variableService.deleteQuestionnaireVariable("lmyoceix", "no-variable"));

        // Then an exception is thrown and no variable has been deleted
        assertEquals(1, variableService.getQuestionnaireVariables("lmyoceix").size());
    }

}
