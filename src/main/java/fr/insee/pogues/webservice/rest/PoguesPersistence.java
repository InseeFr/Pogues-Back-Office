package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Map;

/**
 * WebService class for the Questionnaire Persistence
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
@Api(value = "PoguesPersistence", authorizations = {
	      @Authorization(value="sampleoauth", scopes = {})
	    })
public class PoguesPersistence {

	private QuestionnairesService questionnaireService;

	final static Logger logger = Logger.getLogger(PoguesPersistence.class);

	/**
	 * Dummy GET Helloworld used in unit tests
	 * 
	 * @return "Hello world" as a String
	 */
	@GET
	@Path("helloworld")
	@Produces(MediaType.TEXT_HTML)
	public String helloworld() {
		return "Hello world";
	}

    /**
	 * Gets the questionnaire with id {id}
	 *
	 * @return Response code
	 *
	 *         200: description: Successful response
	 *
	 *         404: description: Questionnaire not found
	 *
	 */
	@GET
	@Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get questionnaire",
    notes = "Gets the questionnaire with id {id}",
    response = String.class)
	public Response getQuestionnaire(@PathParam(value = "id") String id) throws Exception {
		try {
			JSONObject result = this.questionnaireService.getQuestionnaireByID(id);
			return Response.status(Status.OK).entity(result).build();
		} catch(PoguesException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	
	/**
	 * Delete the questionnaire with id {id}
	 * 
	 *
	 *            id
	 * 
	 *            in: path
	 * 
	 *            description: The identifier of the questionnaire to delete
	 * 
	 *            type: string
	 * 
	 * 
	 * @return Response code
	 * 
	 *         200: description: Successful response
	 * 
	 *         404: description: Questionnaire not found
	 *
	 */
	@DELETE
	@Path("questionnaire/{id}")
	@ApiOperation(value = "Get questionnaire",
    notes = "Gets the questionnaire with id {id}",
    response = String.class)
	public Response deleteQuestionnaire(@PathParam(value = "id") String id) throws Exception {
		try {
			this.questionnaireService.deleteQuestionnaireByID(id);
		} catch (Exception e) {
			throw e;
		}
		logger.info("Questionnaire "+ id +" deleted");
		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("questionnaires")
	@ApiOperation(value = "Get questionnaire",
			response = String.class)
	public Response deleteAllQuestionnaires() throws Exception {
		try {
			this.questionnaireService.deleteAllQuestionnaires();
		} catch (Exception e) {
			throw e;
		}
		return Response.status(Status.NO_CONTENT).build();
	}




	/**
	 * Gets the `QuestionnaireList` object.
	 * 
	 * @return Response code
	 * 
	 *         200: description: Successful response 
	 *         
	 *         		schema:  $ref: '#/definitions/QuestionnaireList'
	 * 
	 *         404: description: List not found
	 *
	 */
	@GET
	@Path("questionnaires")
    @Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get questionnaires",
    notes = "Gets the `QuestionnaireList` object",
    response = String.class)
	public Response getQuestionnaireList() {
		try {
			Map<String, JSONObject> questionnaires = this.questionnaireService.getQuestionnaireList();
			if ((questionnaires == null) || (questionnaires.size() == 0)) {
				logger.info("QuestionnaireList not found, returning NOT_FOUND response");
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.status(Status.OK).entity(questionnaires).build();
		} catch(Exception e) {
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}

	}
	
	
	/**
	 * Creates or replaces a `Questionnaire` object.
	 * 
	 * @return 201: description: The questionnaire was created or updated
	 *
	 *         400: description: Malformed object in the query
	 * 
	 *         401: description: The client is not authorized for this operation
	 */
	@PUT
	@Path("questionnaire")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "updateQuestionnaire",
    notes = "Update a `Questionnaire` object",
    response = String.class)
	public Response updateQuestionnaire(JSONObject jsonContent) throws Exception {
        try {
			this.questionnaireService.updateQuestionnaire(jsonContent);
        } catch(PoguesException e){
            throw e;
        } catch (Exception e) {
            throw e;
        }
        String id = (String) jsonContent.get("id");
		logger.info("Questionnaire "+ id +" created");
		return Response.status(Status.NO_CONTENT).build();

	}
	


	/**
	 * Creates a new `Questionnaire`
	 *
	 * @return 201: description: The questionnaire was created
	 * 
	 *         headers: Location: description: The URI of the new questionnaire
	 *         type: string
	 * 
	 *         Slug: description: The id of the questionnaire that was submitted
	 *         type: string
	 * 
	 *         400: description: Malformed object in the query
	 * 
	 *         401: description: The client is not authorized for this operation
	 * 
	 */
	@POST
	@Path("questionnaires")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "createQuestionnaire",
    notes = "Creates a new `Questionnaire`",
    response = String.class)
	public Response createQuestionnaire(JSONObject jsonContent) throws Exception {
        try {
			this.questionnaireService.createQuestionnaire(jsonContent);
        } catch(PoguesException e){
            throw e;
        } catch (Exception e) {
			throw e;
		}
		//TODO return a generic uri
        String id = (String) jsonContent.get("id");
		String uriQuestionnaire = "http://dvrmspogfolht01.ad.insee.intra/rmspogfo/pogues/persistence/questionnaire/"+id;
		logger.info("New questionnaire created , uri :" + uriQuestionnaire);
		return Response.status(Status.CREATED).header("Location", uriQuestionnaire).build();	
	}

	/**
	 * Creates or replaces the `QuestionnaireList` object.

	 * @return 501: description: Not implemented
	 */
	@PUT
	@Path("questionnaires")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "createOrReplaceQuestionnaireList",
    notes = "Creates or replaces the `QuestionnaireList` object",
    response = String.class)
	public Response updateQuestionnaire(String jsonContent) {

		return Response.status(Status.NOT_IMPLEMENTED).build();

	}

	@Autowired
	public void setQuestionnaireService(QuestionnairesService questionnaireService) {
		this.questionnaireService = questionnaireService;
	}

}
