package fr.insee.pogues.persistence;

import fr.insee.pogues.persistence.query.NonUniqueResultException;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;
import fr.insee.pogues.persistence.service.QuestionnairesServiceImpl;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by acordier on 28/07/17.
 */
public class TestQuestionnaireService {

    @Mock
    QuestionnairesServiceQuery questionnairesServiceQuery;

    @InjectMocks
    QuestionnairesServiceImpl questionnairesService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        questionnairesService = spy(new QuestionnairesServiceImpl()); // <- class under test
        initMocks(this);
    }


    @Test//(expected = Exception.class)
    public void emptyListThrowsException() throws Exception {
        exception.expect(PoguesException.class);
        exception.expectMessage("Not found");
        when(questionnairesServiceQuery.getQuestionnaires())
                .thenReturn(new ArrayList<JSONObject>());
        questionnairesService.getQuestionnaireList();

    }

    @Test
    public void getQuestionnaireByOwnerWithNullException() throws Exception{
        exception.expect(PoguesException.class);
        exception.expectMessage("Service unavailable");
        questionnairesService.getQuestionnairesByOwner(null);
    }
    @Test
    public void getQuestionnaireByOwnerWithEmptyException() throws Exception{
        exception.expect(PoguesException.class);
        exception.expectMessage("Service unavailable");
        questionnairesService.getQuestionnairesByOwner("");
    }


    @Test
    public void questionnaireNotFoundThrowsException() throws Exception {
        exception.expect(PoguesException.class);
        exception.expectMessage("Not found");
        when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                .thenReturn(new JSONObject());
        questionnairesService.getQuestionnaireByID("id");

    }

    @Test
    public void ambiguousIdThrowsException() throws Exception {
        exception.expect(NonUniqueResultException.class);
        exception.expectMessage("Test: Exception should propagate");
        when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                .thenThrow(new NonUniqueResultException("Test: Exception should propagate"));
        questionnairesService.getQuestionnaireByID("id");
    }

    @Test
    public void getQuestionnaireById() throws Exception {
        JSONObject q1 = new JSONObject();
        q1.put("id", "foo");
        when(questionnairesServiceQuery.getQuestionnaireByID("foo")).thenReturn(q1);
        JSONObject q2 = questionnairesService.getQuestionnaireByID("foo");
        assertEquals(q1, q2);
        assertEquals("foo", q2.get("id"));

    }


    @Test
    public void listReturnsNormally() throws Exception {
        try {
            when(questionnairesServiceQuery.getQuestionnaires())
                    .thenReturn(new ArrayList<JSONObject>() {
                        {
                            add(new JSONObject());
                        }
                    });
            List<JSONObject> qList = questionnairesService.getQuestionnaireList();
            assertEquals(1, qList.size());
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void deleteExceptionPropagate() throws Exception {
        exception.expect(SQLException.class);
        exception.expectMessage("Test: Exception should propagate");
        doThrow(new SQLException("Test: Exception should propagate"))
                .when(questionnairesServiceQuery)
                .deleteQuestionnaireByID("1");
        questionnairesService.deleteQuestionnaireByID("1");

    }

    @Test
    public void deleteQuestionnaireById() throws Exception {
        doAnswer(invocationOnMock -> null).when(questionnairesServiceQuery).deleteQuestionnaireByID("foo");
        questionnairesService.deleteQuestionnaireByID("foo");
    }
}

