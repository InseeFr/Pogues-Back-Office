package fr.insee.pogues.controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.configuration.properties.ApplicationProperties;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.webservice.rest.QuestionnaireController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static fr.insee.pogues.webservice.rest.QuestionnaireController.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@Slf4j
public class QuestionnaireControllerTest {

    @InjectMocks
    QuestionnaireController questionnaireController;

    @Mock
    QuestionnairesService questionnairesService;

    @Mock
    ApplicationProperties applicationProperties;

    @BeforeEach
    public void beforeEach() {
        questionnaireController =  new QuestionnaireController();
        initMocks(this);
    }

    @Test
    void testCreateBadIdQuestionnaire() throws Exception {
        ObjectNode fakeQuestionnaire = JsonNodeFactory.instance.objectNode();
        fakeQuestionnaire.put("id","bad-id");

        // test if exception is thrown
        PoguesException exception = assertThrows(
                PoguesException.class,
                () -> questionnaireController.createQuestionnaire(fakeQuestionnaire));

        String expectedMessage = BAD_REQUEST;
        String actualMessage = exception.getMessage();
        int expectedStatus = 400;
        int actualStatus = exception.getStatus();
        assertEquals(expectedMessage, actualMessage);
        assertEquals(expectedStatus, actualStatus);

        // test if the method of create questionnaire is never called
        verify(questionnairesService, never()).createQuestionnaire(fakeQuestionnaire);
    }

    @Test
    void testCreateGoodIdQuestionnaire() throws Exception {
        ObjectNode fakeQuestionnaire = JsonNodeFactory.instance.objectNode();
        fakeQuestionnaire.put("id","goodid");
        when(applicationProperties.scheme()).thenReturn("http");
        when(applicationProperties.host()).thenReturn("localhost");
        // test if no exception is thrown
        ResponseEntity responseEntity = assertDoesNotThrow(()->questionnaireController.createQuestionnaire(fakeQuestionnaire));
        // test if the method of create questionnaire is called
        verify(questionnairesService, atMostOnce()).createQuestionnaire(fakeQuestionnaire);
        // test content of response
        assertEquals(1,responseEntity.getHeaders().get("Location").size());
        assertEquals("http://localhost/api/persistence/questionnaire/goodid", responseEntity.getHeaders().get("Location").get(0));
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }


}
