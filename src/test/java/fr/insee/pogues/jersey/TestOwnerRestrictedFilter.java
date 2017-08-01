package fr.insee.pogues.jersey;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.user.service.UserService;
import fr.insee.pogues.webservice.rest.OwnerRestrictedFilter;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.glassfish.jersey.server.ContainerRequest;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestOwnerRestrictedFilter {

    @Mock
    QuestionnairesService questionnaireServicee;

    @Mock
    UserService userService;

    @InjectMocks
    OwnerRestrictedFilter filter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp(){
        filter = spy(new OwnerRestrictedFilter());
        initMocks(this);
    }

    @Test
    public void filterWithNullBodyException() throws IOException {
        exception.expectMessage("Bad Request");
        exception.expect(PoguesException.class);
        ContainerRequest crc = mock(ContainerRequest.class);
        when(crc.readEntity(JSONObject.class))
                .thenReturn(null);
        filter.filter(crc);
    }

    @Test
    public void filterWithNullIdException() throws Exception {
        exception.expectMessage("Bad Request");
        exception.expect(PoguesException.class);
        try {
            filter(new JSONObject());
        } catch(Exception e) {
            assertEquals("Missing attribute 'id' in Request Payload", ((PoguesException)e).toRestMessage().getDetails());
            throw e;
        }


    }


    @Test
    public void filterWithNullOwnerException() throws Exception {
        exception.expectMessage("Bad Request");
        exception.expect(PoguesException.class);
        JSONObject payload = new JSONObject();
        payload.put("id", "foo");
        Mockito.when(questionnaireServicee.getQuestionnaireByID("foo"))
                .thenReturn(payload);
        filter(payload);
    }

    private void filter(JSONObject payload) throws Exception  {
        ContainerRequest crc = mock(ContainerRequest.class);
        when(crc.readEntity(JSONObject.class))
                .thenReturn(payload);
        filter.filter(crc);
    }


}
