package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.configuration.properties.ApplicationProperties;
import fr.insee.pogues.exception.QuestionnaireIdentifierException;
import fr.insee.pogues.service.ModelValidationService;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireControllerTest {

    private QuestionnaireController questionnaireController;
    private QuestionnaireServiceStub questionnaireServiceStub;

    @BeforeEach
    void beforeEach() {
        ApplicationProperties fooProperties = new ApplicationProperties("localhost", "http", null, null, null, null, null);
        questionnaireServiceStub = new QuestionnaireServiceStub();
        questionnaireController = new QuestionnaireController(fooProperties, questionnaireServiceStub, null, null, null, null, new ModelValidationService());
    }

    @Test
    void testCreateBadIdQuestionnaire() {
        // Given
        // a questionnaire with a valid id
        ObjectNode fakeQuestionnaire = JsonNodeFactory.instance.objectNode();
        fakeQuestionnaire.put("id","bad-id"); // '-' is not allowed in id

        // When
        // calling the "create questionnaire" controller
        // test if exception is thrown
        QuestionnaireIdentifierException identifierException = assertThrows(
                QuestionnaireIdentifierException.class,
                () -> questionnaireController.createQuestionnaire(fakeQuestionnaire));

        // Then
        assertEquals("Identifier bad-id is invalid.", identifierException.getMessage());

        // test if the method of create questionnaire is never called
        assertEquals(0, questionnaireServiceStub.getGetCreateQuestionnaireCalls());
    }

    @Test
    void testCreateGoodIdQuestionnaire() {
        // Given
        // a questionnaire with a valid id
        ObjectNode fakeQuestionnaire = JsonNodeFactory.instance.objectNode();
        String goodId = "foo12345";
        fakeQuestionnaire.put("id", goodId);
        // When
        // calling the "create questionnaire" controller
        // test that no exception is thrown
        ResponseEntity<Object> responseEntity = assertDoesNotThrow(() ->
                questionnaireController.createQuestionnaire(fakeQuestionnaire));

        // Then
        // test if the method of create questionnaire is called
        assertEquals(1, questionnaireServiceStub.getGetCreateQuestionnaireCalls());
        // test content of response
        List<String> location = responseEntity.getHeaders().get("Location");
        assertNotNull(location);
        assertEquals(1, location.size());
        assertEquals("http://localhost/api/persistence/questionnaire/foo12345", location.getFirst());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

}
