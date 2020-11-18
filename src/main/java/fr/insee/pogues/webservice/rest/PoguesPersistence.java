package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * WebService class for the Instrument Persistence
 * 
 * See the swagger documentation for this service :
 * http://inseefr.github.io/Pogues/en/remote-apis/swagger.html
 * 
 * @author I6VWID
 * 
 *         schemes: - http
 * 
 *         consumes: - application/json
 * 
 *         produces: - application/json
 *
 */
@Component
@Path("/persistence")
@Api(value = "Pogues Persistence")
public class PoguesPersistence {

    final static Logger logger = LogManager.getLogger(PoguesPersistence.class);

    @Autowired
	private QuestionnairesService questionnaireService;


	@GET
	@Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Get questionnaire",
            notes = "Gets the questionnaire with id {id}",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
	public Response getQuestionnaire(
			@ApiParam(value = "This is the id of the object we want to retrieve", required = true)
			@PathParam(value = "id") String id
	) throws Exception {
		try {
			JSONObject result = questionnaireService.getQuestionnaireByID(id);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

	}

    @GET
    @Path("questionnaires/search")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Search questionnaires",
            notes = "Search questionnaires matching query params",
            response = List.class
    )
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad request")
	})
    public Response searchQuestionnaires(
			@ApiParam(value = "A user id matching owner permission on each object of the collection", required = false)
            @QueryParam("owner") String owner
    ) throws Exception {
        try {
			List<JSONObject> questionnaires = new ArrayList<>();
            if(null != owner){
                questionnaires.addAll(questionnaireService.getQuestionnairesByOwner(owner));
            }
            return Response.status(Status.OK).entity(questionnaires).build();
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
    }
    
	@GET
	@Path("questionnaires/search/meta")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Get questionnaires' metadata",
            notes = "Get questionnaires' metadata matching query params",
            response = List.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")
    })
	public Response getQuestionnairesMetadata(
			@ApiParam(value = "A user id matching owner permission on each object of the collection", required = false)
            @QueryParam("owner") String owner
	) throws Exception {
		try {
			List<JSONObject> questionnairesMetadata = new ArrayList<>();
            if(null != owner){
                questionnairesMetadata.addAll(questionnaireService.getQuestionnairesMetadata(owner));
            }
            return Response.status(Status.OK).entity(questionnairesMetadata).build();
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
	}

	@DELETE
	@Path("questionnaire/{id}")
	@ApiOperation(
	        value = "Delete questionnaire",
            notes = "Delete questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found")
    })
	@OwnerRestricted
	public Response deleteQuestionnaire(
			@ApiParam(value = "The id of the object that need to be deleted", required = true)
			@PathParam(value = "id") String id
	) throws Exception {
		try {
			questionnaireService.deleteQuestionnaireByID(id);
			logger.info("Questionnaire "+ id +" deleted");
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("questionnaires")
    @Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(
	        value = "Get questionnaires",
            notes = "Gets the `QuestionnaireList` object",
            response = List.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
	public Response getQuestionnaireList() throws Exception {
		try {
			List<JSONObject> questionnaires = questionnaireService.getQuestionnaireList();
			return Response.status(Status.OK).entity(questionnaires).build();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@PUT
	@Path("questionnaire/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Update questionnaire",
            notes = "Update a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
	@OwnerRestricted
	public Response updateQuestionnaire(
			@ApiParam(value = "The id of the object that need to be updated", required = true)
			@PathParam(value = "id") String id,
			@ApiParam(value = "Instrument object to be updated") JSONObject jsonContent
	) throws Exception {
        try {
			questionnaireService.updateQuestionnaire(id, jsonContent);
			logger.info("Questionnaire "+ id +" updated");
			return Response.status(Status.NO_CONTENT).build();
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
	}

	@POST
	@Path("questionnaires")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Create Questionnaire",
            notes = "Creates a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Entity already exists")
    })
	public Response createQuestionnaire(
			@ApiParam(value = "New Instrument Object", required = true) JSONObject jsonContent
	) throws Exception {
        try {
			questionnaireService.createQuestionnaire(jsonContent);
			//TODO return a generic uri
			String id = (String) jsonContent.get("id");
			String uriQuestionnaire = "http://dvrmspogfolht01.ad.insee.intra/rmspogfo/pogues/persistence/questionnaire/"+id;
			logger.debug("New questionnaire created , uri :" + uriQuestionnaire);
			return Response.status(Status.CREATED).header("Location", uriQuestionnaire).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}
