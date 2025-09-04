package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.QuestionnaireNotFoundException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceTest {

    @Mock
    QuestionnaireRepository questionnairesServiceQuery;

    @InjectMocks
    QuestionnaireService questionnaireService;

    @Mock
    VersionService versionService;

    @Test
    void getQuestionnaireByOwnerWithNullException() throws Exception{
        Throwable exception = assertThrows(PoguesException.class,()->questionnaireService.getQuestionnairesByOwner(null));
        assertEquals("Bad Request",exception.getMessage());
    }
    
    @Test
    void getQuestionnaireByOwnerWithEmptyException() throws Exception{
        Throwable exception = assertThrows(PoguesException.class,()->questionnaireService.getQuestionnairesByOwner(""));
        assertEquals("Bad Request",exception.getMessage());
    }


    @Test
    void questionnaireNotFoundThrowsException() throws Exception {
        when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                .thenReturn(null);
        assertThrows(QuestionnaireNotFoundException.class, ()->questionnaireService.getQuestionnaireByID("id"));
    }

    @Test
    void ambiguousIdThrowsException() throws Exception {
        when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                .thenThrow(new NonUniqueResultException("Test: Exception should propagate"));
        Throwable exception = assertThrows(NonUniqueResultException.class,()->questionnaireService.getQuestionnaireByID("id"));
        assertEquals("Test: Exception should propagate",exception.getMessage());
    }

    @Test
    void getQuestionnaireById() throws Exception {
        ObjectNode q1 = JsonNodeFactory.instance.objectNode();
        q1.put("id", "foo");
        q1.putArray("Control");
        q1.putArray("Child");
        when(questionnairesServiceQuery.getQuestionnaireByID("foo")).thenReturn(q1);
        JsonNode q2 = questionnaireService.getQuestionnaireByID("foo");
        assertEquals(q1, q2);
        assertEquals("foo", q2.get("id").asText());

    }

    @Test
    void deleteExceptionPropagate() throws Exception {
        doThrow(new SQLException("Test: Exception should propagate"))
                .when(questionnairesServiceQuery)
                .deleteQuestionnaireByID("1");
        Throwable exception = assertThrows(SQLException.class,()->questionnaireService.deleteQuestionnaireByID("1"));
        assertEquals("Test: Exception should propagate",exception.getMessage());

    }

    @Test
    void deleteQuestionnaireById() throws Exception {
        doAnswer(invocationOnMock -> null).when(questionnairesServiceQuery).deleteQuestionnaireByID("foo");
        doAnswer(invocationOnMock -> null).when(versionService).deleteAllVersionsByQuestionnaireIdExceptLast("foo");
        questionnaireService.deleteQuestionnaireByID("foo");
    }
}

