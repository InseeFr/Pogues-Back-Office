package fr.insee.pogues.webservice.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Pogues Persistence")
public class PoguesPersistence {

    final static Logger logger = LogManager.getLogger(PoguesPersistence.class);

    @Autowired
	private QuestionnairesService questionnaireService;


    @GET
	@Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId  = "getQuestionnaires",
	        summary = "Get questionnaire",
            description = "Gets the questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })	
	public Response getQuestionnaire(
			@Parameter(description = "This is the id of the object we want to retrieve", required = true)
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
    @Operation(
    		operationId = "searchQuestionnaires",
            summary = "Search questionnaires",
            description = "Search questionnaires matching query params"
    )
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "400", description = "Bad request")
	})
    public Response searchQuestionnaires(
			@Parameter(description = "A user id matching owner permission on each object of the collection", required = false)
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
	@Operation(
			operationId = "searchQuestionnairesMetadata",
	        summary = "Get questionnaires' metadata",
            description = "Get questionnaires' metadata matching query params"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
	public Response getQuestionnairesMetadata(
			@Parameter(description = "A user id matching owner permission on each object of the collection", required = false)
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
	
	@GET
	@Path("questionnaires/stamps")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "searchQuestionnairesStamps",
	        summary = "Get stamps in database",
            description = "Get stamps with at least one questionnaire saved in database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
	public Response getQuestionnaireStamps() throws Exception {
		try {
			List<JSONObject> questionnairesStamps = new ArrayList<>();
			questionnairesStamps.addAll(questionnaireService.getQuestionnairesStamps());
            return Response.status(Status.OK).entity(questionnairesStamps).build();
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
	}
	

	@DELETE
	@Path("questionnaire/{id}")
	@Operation(
			operationId = "deleteQuestionnaire",
	        summary = "Delete questionnaire",
            description = "Delete questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public Response deleteQuestionnaire(
			@Parameter(description = "The id of the object that need to be deleted", required = true)
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
	@Operation(
			operationId = "getQuestionnaireList",
	        summary = "Get questionnaires",
            description = "Gets the `QuestionnaireList` object"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
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
	@Operation(
			operationId = "updateQuestionnaire",
	        summary = "Update questionnaire",
            description = "Update a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public Response updateQuestionnaire(
			@Parameter(description = "The id of the object that need to be updated", required = true)
			@PathParam(value = "id") String id,
			@Parameter(description = "Instrument object to be updated") JSONObject jsonContent
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
	@Operation(
			operationId = "createQuestionnaire",
	        summary = "Create Questionnaire",
            description = "Creates a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Entity already exists")
    })
	public Response createQuestionnaire(
			@Parameter(description = "New Instrument Object", required = true) JSONObject jsonContent
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
