package fr.insee.pogues.webservice.rest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoguesExceptionMapperTest {

    PoguesExceptionMapper mapper;

    @BeforeEach
    public void setUp(){
        mapper = spy(new PoguesExceptionMapper());
    }

	@Test
	void checkToResponseOutput() {
		PoguesException e = new PoguesException(418, "I'm a teapot", "Strange things happen");
		RestMessage m = e.toRestMessage();
		Response resp = mapper.toResponse(e);
		assertAll("checkToResponseOutput", 
				() -> assertEquals(MediaType.APPLICATION_JSON_TYPE, resp.getMediaType()),
				() -> assertEquals(m.getMessage(), ((RestMessage) resp.getEntity()).getMessage()),
				() -> assertEquals(m.getDetails(), ((RestMessage) resp.getEntity()).getDetails()),
				() -> assertEquals(418, resp.getStatus()));
	}
}
    
