package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.junit.jupiter.api.Assertions.*;

class PublicEnemyVariableServiceTest {

    @Test
    void getVariables() throws Exception {
        // Given
        // Read tested questionnaire
        URL url = this.getClass().getClassLoader().getResource(
                "persistence/VariablesService/l4i3m6qa.json");
        assert url != null;
        String stringQuestionnaire = Files.readString(Path.of(url.toURI()));
        JsonNode jsonQuestionnaire = jsonStringtoJsonNode(stringQuestionnaire);
        // Mock questionnaire service
        QuestionnaireService questionnaireService = Mockito.mock(QuestionnaireService.class);
        Mockito.when(questionnaireService.getQuestionnaireByID("l4i3m6qa")).thenReturn(jsonQuestionnaire);

        // When
        PublicEnemyVariableService publicEnemyVariableService = new PublicEnemyVariableService(questionnaireService);
        JsonNode resultAsJson = publicEnemyVariableService.getVariablesByQuestionnaire("l4i3m6qa");
        String result = resultAsJson.toString();

        // Then
        // (quick and dirty tests, the implementation could be refactored to make it more easily testable)
        assertNotNull(result);
        assertNotEquals("", result);
        // Input questionnaire contains all three types of variables, these should be in output
        assertTrue(result.contains("CollectedVariableType"));
        assertTrue(result.contains("CalculatedVariableType"));
        assertTrue(result.contains("ExternalVariableType"));
        // Variable names present in input questionnaire, these should be in output
        List.of("CAT_VAR_COMP_1", "CAT_VAR_EXT_1", "CAT_Q1", "CAT_Q2", "CAT_Q21", "CAT_Q22", "CAT_Q23")
                .forEach(variableName -> assertTrue(result.contains(variableName)));
    }

    @Test
    void getVariablesForPublicEnemy() throws Exception {
        // Given
        // Read tested questionnaire
        URL url = this.getClass().getClassLoader().getResource(
                "persistence/VariablesService/l4i3m6qa.json");
        assert url != null;
        String stringQuestionnaire = Files.readString(Path.of(url.toURI()));
        JsonNode jsonQuestionnaire = jsonStringtoJsonNode(stringQuestionnaire);
        // Mock questionnaire service
        QuestionnaireService questionnaireService = Mockito.mock(QuestionnaireService.class);
        Mockito.when(questionnaireService.getQuestionnaireByIDWithReferences("l4i3m6qa")).thenReturn(jsonQuestionnaire);

        // When
        PublicEnemyVariableService publicEnemyVariableService = new PublicEnemyVariableService(questionnaireService);
        ArrayNode result = publicEnemyVariableService.getVariablesByQuestionnaireForPublicEnemy("l4i3m6qa");

        // Then
        assertNotNull(result);
        assertEquals(7, result.size());
    }

    /** Custom exception with no stack trace (to not pollute log when running tests) to be used in mocking. */
    static class MockedException extends Exception {
        public MockedException() {
            super("Mocked exception.", null, true, false);
        }
    }

    @Test
    void getVariables_exceptionDuringQuestionnaireQuery_shouldReturnNull() throws Exception {
        // Given
        QuestionnaireService questionnaireService = Mockito.mock(QuestionnaireService.class);
        Mockito.when(questionnaireService.getQuestionnaireByID("foo-id")).thenThrow(new MockedException());

        // When
        PublicEnemyVariableService publicEnemyVariableService = new PublicEnemyVariableService(questionnaireService);
        JsonNode result = publicEnemyVariableService.getVariablesByQuestionnaire("foo-id");

        // Then
        assertNull(result);
    }

    @Test
    void getVariablesForPublicEnemy_exceptionDuringQuestionnaireQuery_shouldReturnNull() throws Exception {
        // Given
        QuestionnaireService questionnaireService = Mockito.mock(QuestionnaireService.class);
        Mockito.when(questionnaireService.getQuestionnaireByID("foo-id")).thenThrow(new MockedException());

        // When
        PublicEnemyVariableService publicEnemyVariableService = new PublicEnemyVariableService(questionnaireService);
        ArrayNode result = publicEnemyVariableService.getVariablesByQuestionnaireForPublicEnemy("foo-id");

        // Then
        assertNull(result);
    }

}
