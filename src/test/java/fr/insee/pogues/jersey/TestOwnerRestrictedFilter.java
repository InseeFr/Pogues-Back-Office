package fr.insee.pogues.jersey;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.user.model.User;
import fr.insee.pogues.user.query.UserServiceQuery;
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

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestOwnerRestrictedFilter {

    @Mock
    QuestionnairesService questionnaireService;

    @Mock
    UserServiceQuery userServiceQuery;

    @InjectMocks
    OwnerRestrictedFilter filter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp(){
        filter = spy(new OwnerRestrictedFilter());
        initMocks(this);
    }

    @Test // No payload attached =>  400|Bad Request
    public void filterWithNullBodyException() throws Exception {
        exception.expectMessage("Bad Request");
        exception.expect(PoguesException.class);
        try {
            filter(null);
        } catch(Exception e) {
            String detailedMessage = ((PoguesException)e).toRestMessage().getDetails();
            assertEquals("No body attached to request", detailedMessage);
            throw e;
        }
    }

    @Test // Id not found in payload =>  400|Bad Request
    public void filterWithNullIdException() throws Exception {
        exception.expectMessage("Bad Request");
        exception.expect(PoguesException.class);
        try {
            filter(new JSONObject());
        } catch(Exception e) {
            String detailedMessage = ((PoguesException)e).toRestMessage().getDetails();
            assertEquals("Missing attribute 'id' in Request Payload", detailedMessage);
            throw e;
        }
    }

    @Test// Owner not found in payload =>  400|Bad Request
    public void filterWithNullOwnerException() throws Exception {
        exception.expectMessage("Bad Request");
        exception.expect(PoguesException.class);
        JSONObject payload = new JSONObject();
        payload.put("id", "foo");
        Mockito.when(questionnaireService.getQuestionnaireByID("foo"))
                .thenReturn(payload);
        try {
            filter(payload);
        } catch(Exception e) {
            String detailedMessage = ((PoguesException)e).toRestMessage().getDetails();
            assertEquals("Request payload does not have an owner attribute",
                    detailedMessage);
            throw e;
        }
    }

    @Test
    public void filterWithUnauthorizedException() throws Exception {
        exception.expectMessage("Unauthorized");
        exception.expect(PoguesException.class);
        try {
            JSONObject payload = new JSONObject();
            payload.put("id", "ENTITY-ID");
            payload.put("owner", "A-USER-GROUP");
            ContainerRequest crc = mock(ContainerRequest.class);
            when(crc.readEntity(JSONObject.class))
                    .thenReturn(payload);
            when(crc.getSecurityContext()).thenReturn(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return /* principal name */() -> "CURRENT-USER-ID";
                }

                @Override
                public boolean isUserInRole(String s) {
                    return false;
                }

                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public String getAuthenticationScheme() {
                    return null;
                }
            });
            JSONObject storedEntity = new JSONObject();
            storedEntity.put("owner", "ANOTHER-GROUP");
            when(questionnaireService.getQuestionnaireByID("ENTITY-ID"))
                    .thenReturn(storedEntity);
            when(userServiceQuery.getNameAndPermissionByID("CURRENT-USER-ID"))
                    .thenReturn(new User("CURRENT-USER-ID",
                            "John Doe",
                            "John",
                            "Doe",
                            "NON-MATCHING-GROUP"));
            filter.filter(crc);
        } catch(Exception e) {
            String detailedMessage = ((PoguesException)e).toRestMessage().getDetails();
            int status = ((PoguesException)e).toRestMessage().getStatus();
            assertEquals("This object is not yours", detailedMessage);
            assertEquals(403, status);
            throw e;
        }
    }

    @Test
    public void filterWithUnauthenticatedException() throws Exception {
        exception.expectMessage("Unauthenticated");
        exception.expect(PoguesException.class);
        try {
            JSONObject payload = new JSONObject();
            payload.put("id", "ENTITY-ID");
            payload.put("owner", "A-USER-GROUP");
            ContainerRequest crc = mock(ContainerRequest.class);
            when(crc.readEntity(JSONObject.class))
                    .thenReturn(payload);
            when(crc.getSecurityContext()).thenReturn(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return null;
                }

                @Override
                public boolean isUserInRole(String s) {
                    return false;
                }

                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public String getAuthenticationScheme() {
                    return null;
                }
            });
            when(questionnaireService.getQuestionnaireByID("ENTITY-ID"))
                    .thenReturn(payload);
            filter.filter(crc);
        } catch(Exception e) {
            String detailedMessage = ((PoguesException)e).toRestMessage().getDetails();
            int status = ((PoguesException)e).toRestMessage().getStatus();
            assertEquals("You are not logged in", detailedMessage);
            assertEquals(401, status);
            throw e;
        }
    }

    private void filter(JSONObject payload) throws Exception  {
        ContainerRequest crc = mock(ContainerRequest.class);
        when(crc.readEntity(JSONObject.class))
                .thenReturn(payload);
        filter.filter(crc);
    }


}
