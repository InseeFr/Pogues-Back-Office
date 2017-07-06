package fr.insee.pogues.webservice.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by acordier on 04/07/17.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private final Status STATUS = Status.INTERNAL_SERVER_ERROR;

    public Response toResponse(Throwable error){
        RestMessage message = new RestMessage(
                STATUS.getStatusCode(),
                "An unexpected error occured", error.getMessage());
        return Response.status(STATUS)
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
