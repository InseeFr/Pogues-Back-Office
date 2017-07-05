package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * WebService class for the Identity Service
 *
 * @author I6VWID
 *         <p>
 *         schemes: - http
 *         <p>
 *         consumes: - application/json
 *         <p>
 *         produces: - application/json
 */
@Component
@Path("/user")
@Api(value = "PoguesUser", authorizations = {
        @Authorization(value = "sampleoauth", scopes = {})
})
public class PoguesUser {


    @Context
    HttpServletRequest request;

    private UserService userService;

    final static Logger logger = Logger.getLogger(PoguesUser.class);

    /**
     * Dummy GET Helloworld used in unit tests
     *
     * @return "Hello world" as a String
     */
    @GET
    @Path("helloworld")
    @ApiOperation(value = "Hello world",
            notes = "Dummy GET Helloworld, used in unit tests",
            response = String.class)
    public String helloworld() {
        return "Hello world";
    }

    /**
     * Get the user id of the connected user
     *
     * @return Response code
     * <p>
     * 200: description: Successful response
     * <p>
     * 404: description: Questionnaire not found
     */
    @GET
    @Path("id")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getID",
            notes = "Get the user id of the connected user",
            response = String.class)
    public Response getID() {
        String jsonResultat = this.userService.getUserID(request);
        if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
            logger.info("No user connected , returning NOT_FOUND response");
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(jsonResultat).build();
    }

    /**
     * Get the user attribute of the connected user
     *
     * @return Response code
     * <p>
     * 200: description: Successful response
     * <p>
     * 404: description: Questionnaire not found
     */
    @GET
    @Path("attributes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttribute() {
        String jsonResultat = this.userService.getNameAndPermission(request);
        if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
            logger.info("No user connected , returning NOT_FOUND response");
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(jsonResultat).build();
    }

    /**
     * Get the user permissions list
     *
     * @return Response code
     *
     * 200: description: Successful response
     */
    @GET
    @Path("permissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPermissions() {
        try {
            return Response.ok(this.userService.getPermissions()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
