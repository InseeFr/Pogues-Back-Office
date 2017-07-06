package fr.insee.pogues.webservice.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by acordier on 04/07/17.
 */
@Provider
public class PoguesExceptionMapper implements ExceptionMapper<PoguesException> {
    public Response toResponse(PoguesException ex) {
        RestMessage message = ex.toRestMessage();
        return Response.status(message.getStatus())
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
