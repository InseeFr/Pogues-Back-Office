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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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

    @Autowired
    Environment env;
    
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
	@Path("questionnaire/json-lunatic/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Get questionnaire",
            notes = "Gets the questionnaire with id JsonLunatic {id}",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
	public Response getJsonLunatic(
			@ApiParam(value = "This is the id of the object we want to retrieve", required = true)
			@PathParam(value = "id") String id
	) throws Exception {
		try {
			JSONObject result = questionnaireService.getJsonLunaticByID(id);
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
	
	@DELETE
	@Path("questionnaire/json-lunatic/{id}")
	@ApiOperation(
	        value = "Delete Json Lunatic of a questionnaire",
            notes = "Delete Json Lunatic of a  questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found")
    })
//	@OwnerRestricted
	public Response deleteJsonLunatic(
			@ApiParam(value = "The id of the object that need to be deleted", required = true)
			@PathParam(value = "id") String id
	) throws Exception {
		try {
			questionnaireService.deleteJsonLunaticByID(id);
			logger.info("Questionnaire "+ id +" deleted");
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception e) {
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
	@Path("questionnaire/json-lunatic/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Update Json Lunatic",
            notes = "Update Json Lunatic of a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
//	@OwnerRestricted
	public Response updateJsonLunatic(
			@ApiParam(value = "The id of the questionnaire which json lunatic needs to be updated", required = true)
			@PathParam(value = "id") String id,
			@ApiParam(value = "Json Lunatic to be updated") JSONObject jsonLunatic
	) throws Exception {
        try {
			questionnaireService.updateJsonLunatic(id, jsonLunatic);
			logger.info("Json Lunatic of questionnaire "+ id +" updated");
			return Response.status(Status.NO_CONTENT).build();
        } catch (Exception e) {
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
			String id = (String) jsonContent.get("id");
			String dbHost = env.getProperty("fr.insee.pogues.persistence.database.host");
			String apiName = env.getProperty("fr.insee.pogues.api.name");
			String uriQuestionnaire = String.format("http://%s%s/persistence/questionnaire/%s",dbHost,apiName,id);
			logger.debug("New questionnaire created , uri :" + uriQuestionnaire);
			return Response.status(Status.CREATED).header("Location", uriQuestionnaire).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@POST
	@Path("questionnaires/json-lunatic")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(
	        value = "Create Json Lunatic of questionnaire",
            notes = "Creates a new Json Lunatic entry"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Entity already exists")
    })
	public Response createJsonLunatic(
			@ApiParam(value = "New Instrument Object", required = true) JSONObject jsonContent
	) throws Exception {
        try {
			questionnaireService.createJsonLunatic(jsonContent);
			String id = (String) jsonContent.get("id");
			String dbHost = env.getProperty("fr.insee.pogues.persistence.database.host");
			String apiName = env.getProperty("fr.insee.pogues.api.name");
			String uriJsonLunaticQuestionnaire = String.format("http://%s%s/persistence/questionnaire/json-lunatic/%s",dbHost,apiName,id);
			logger.debug("New Json Lunatic created , uri :" + uriJsonLunaticQuestionnaire);
			return Response.status(Status.CREATED).header("Location", uriJsonLunaticQuestionnaire).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}
