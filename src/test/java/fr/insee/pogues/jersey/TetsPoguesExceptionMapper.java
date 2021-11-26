package fr.insee.pogues.jersey;

import fr.insee.pogues.webservice.rest.PoguesException;
import fr.insee.pogues.webservice.rest.PoguesExceptionMapper;
import fr.insee.pogues.webservice.rest.RestMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public class TetsPoguesExceptionMapper {

    PoguesExceptionMapper mapper;

    @BeforeEach
    public void setUp(){
        mapper = spy(new PoguesExceptionMapper());
    }

    @Test
    public void toResponse(){
        PoguesException e = new PoguesException(418, "I'm a teapot", "Strange things happen");
        RestMessage m = e.toRestMessage();
        Response resp = mapper.toResponse(e);
        assertEquals(MediaType.APPLICATION_JSON_TYPE, resp.getMediaType());
        assertEquals(m.getMessage(), ((RestMessage)resp.getEntity()).getMessage());
        assertEquals(m.getDetails(), ((RestMessage)resp.getEntity()).getDetails());
        assertEquals(418, resp.getStatus());
    }
}
