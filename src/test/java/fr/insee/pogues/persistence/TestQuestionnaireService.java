package fr.insee.pogues.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.persistence.impl.NonUniqueResultException;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import fr.insee.pogues.persistence.service.QuestionnairesServiceImpl;
import fr.insee.pogues.persistence.service.VersionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestQuestionnaireService {

    @Mock
    QuestionnaireRepository questionnairesServiceQuery;

    @InjectMocks
    QuestionnairesServiceImpl questionnairesService;

    @Mock
    VersionServiceImpl versionService;
    @Test
    void getQuestionnaireByOwnerWithNullException() throws Exception{
        Throwable exception = assertThrows(PoguesException.class,()->questionnairesService.getQuestionnairesByOwner(null));
        assertEquals("Bad Request",exception.getMessage());
    }
    
    @Test
    void getQuestionnaireByOwnerWithEmptyException() throws Exception{
        Throwable exception = assertThrows(PoguesException.class,()->questionnairesService.getQuestionnairesByOwner(""));
        assertEquals("Bad Request",exception.getMessage());
    }


    @Test
    void questionnaireNotFoundThrowsException() throws Exception {
        when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                .thenReturn(null);
        Throwable exception = assertThrows(PoguesException.class,()->questionnairesService.getQuestionnaireByID("id"));
        assertEquals("Not found",exception.getMessage());

    }

    @Test
    void ambiguousIdThrowsException() throws Exception {
        when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                .thenThrow(new NonUniqueResultException("Test: Exception should propagate"));
        Throwable exception = assertThrows(NonUniqueResultException.class,()->questionnairesService.getQuestionnaireByID("id"));
        assertEquals("Test: Exception should propagate",exception.getMessage());
    }

    @Test
    void getQuestionnaireById() throws Exception {
        ObjectNode q1 = JsonNodeFactory.instance.objectNode();
        q1.put("id", "foo");
        when(questionnairesServiceQuery.getQuestionnaireByID("foo")).thenReturn(q1);
        JsonNode q2 = questionnairesService.getQuestionnaireByID("foo");
        assertEquals(q1, q2);
        assertEquals("foo", q2.get("id").asText());

    }

    @Test
    void deleteExceptionPropagate() throws Exception {
        doThrow(new SQLException("Test: Exception should propagate"))
                .when(questionnairesServiceQuery)
                .deleteQuestionnaireByID("1");
        Throwable exception = assertThrows(SQLException.class,()->questionnairesService.deleteQuestionnaireByID("1"));
        assertEquals("Test: Exception should propagate",exception.getMessage());

    }

    @Test
    void deleteQuestionnaireById() throws Exception {
        doAnswer(invocationOnMock -> null).when(questionnairesServiceQuery).deleteQuestionnaireByID("foo");
        doAnswer(invocationOnMock -> null).when(versionService).deleteAllVersionsByQuestionnaireIdExceptLast("foo");
        questionnairesService.deleteQuestionnaireByID("foo");
    }
}

