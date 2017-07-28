package fr.insee.pogues.persistence;

import fr.insee.pogues.persistence.query.NonUniqueResultException;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;
import fr.insee.pogues.persistence.service.QuestionnairesServiceImpl;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Before
    public void setUp() {
        questionnairesService = spy(new QuestionnairesServiceImpl()); // <- class under test
        initMocks(this);
    }

    @Test(expected = Exception.class)
    public void emptyListThrowsException() throws Exception {
        try {
            when(questionnairesServiceQuery.getQuestionnaires())
                    .thenReturn(new ArrayList<JSONObject>());
            questionnairesService.getQuestionnaireList();
        } catch (Exception e) {
            assertTrue(e instanceof PoguesException);
            assertEquals("Not found", e.getMessage());
            throw e;
        }
    }

    @Test(expected = Exception.class)
    public void questionnaireNotFoundThrowsException() throws Exception {
        try {
            when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                    .thenReturn(new JSONObject());
            questionnairesService.getQuestionnaireByID("id");
        } catch (Exception e) {
            assertTrue(e instanceof PoguesException);
            assertEquals("Not found", e.getMessage());
            throw e;
        }
    }

    @Test(expected = Exception.class)
    public void ambiguousIdThrowsException() throws Exception {
        try {
            when(questionnairesServiceQuery.getQuestionnaireByID("id"))
                    .thenThrow(new NonUniqueResultException("!!!"));
            questionnairesService.getQuestionnaireByID("id");
        } catch (Exception e) {
            assertTrue(e instanceof NonUniqueResultException);
            assertEquals("!!!", e.getMessage());
            throw e;
        }
    }


    @Test
    public void listReturnsNormally() throws Exception {
        try {
            when(questionnairesServiceQuery.getQuestionnaires())
                    .thenReturn(new ArrayList<JSONObject>(){
                        {add(new JSONObject());}
                    });
            List<JSONObject> qList = questionnairesService.getQuestionnaireList();
            assertEquals(1, qList.size());
        } catch (Exception e) {
            throw e;
        }
    }

    @Test()
    public void deleteExceptionPropagate() throws Exception {
        try {
            doThrow(new SQLException("!!!"))
                    .when(questionnairesServiceQuery)
                    .deleteQuestionnaireByID("1");
            questionnairesService.deleteQuestionnaireByID("1");
        } catch(Exception e){
            assertTrue(e instanceof SQLException);
            assertEquals("!!!", e.getMessage());
        }
    }



}

