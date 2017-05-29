package fr.insee.pogues.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import fr.insee.pogues.persistence.service.QuestionnairesService;

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
@Path("/persistence")
public class PoguesPersistence {

	final static Logger logger = Logger.getLogger(PoguesPersistence.class);

	/**
	 * Dummy GET Helloworld used in unit tests
	 * 
	 * @return "Hello world" as a String
	 */
	@GET
	@Path("helloworld")
	public String helloworld() {
		return "Hello world";
	}

	/**
	 * Gets the questionnaire with id {id}
	 * 
	 * @param name:
	 *            id
	 * 
	 *            in: path
	 * 
	 *            description: The identifier of the questionnaire to retrieve
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
	@GET
	@Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionnaire(@PathParam(value = "id") String id) {
		QuestionnairesService service = new QuestionnairesService();
		String jsonResultat = service.getQuestionnaireByID(id);
		service.close();
		if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
    		logger.info("Questionnaire not found, returning NOT_FOUND response");
    		return Response.status(Status.NOT_FOUND).build();
    	}
		return Response.status(Status.OK).entity(jsonResultat).build();
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
	public Response getQuestionnaireList() {
		QuestionnairesService service = new QuestionnairesService();
		String jsonResultat = service.getQuestionnaireList();
		service.close();
		if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
    		logger.info("QuestionnaireList not found, returning NOT_FOUND response");
    		return Response.status(Status.NOT_FOUND).build();
    	}
		return Response.status(Status.OK).entity(jsonResultat).build();	
	}
	
	
	/**
	 * Creates or replaces a `Questionnaire` object.
	 * 
	 * @param name:
	 *            id
	 * 
	 *            in: path
	 * 
	 *            description: The identifier of the questionnaire to create or
	 *            save
	 * 
	 *            type: string
	 * 
	 * @param name:
	 *            questionnaire
	 * 
	 *            in: body
	 * 
	 *            description: The questionnaire to save
	 * 
	 *            required: true
	 *
	 * 
	 * @return 201: description: The questionnaire was created or updated
	 *
	 *         400: description: Malformed object in the query
	 * 
	 *         401: description: The client is not authorized for this operation
	 */
	@PUT
	@Path("questionnaire/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrReplaceQuestionnaire(@PathParam(value = "id") String id, String jsonContent) {

		if ((jsonContent == null) || (jsonContent.length() == 0)) {
    		logger.error("Null or empty content received, returning BAD REQUEST response");
    		return Response.status(Status.BAD_REQUEST).build();
    	}
		QuestionnairesService service = new QuestionnairesService();
		service.createOrReplaceQuestionnaire(id,jsonContent);
		service.close();
		logger.info("Questionnaire "+ id +" created");
		return Response.status(Status.CREATED).build();

	}
	
	
//	/**
//	 * Delete a `Questionnaire` object.
//	 * 
//	 * @param name:
//	 *            id
//	 * 
//	 *            in: path
//	 * 
//	 *            description: The identifier of the questionnaire to create or
//	 *            save
//	 * 
//	 *            type: string
//	 * 
//	 * 
//	 * @return 200: description: The questionnaire was deleted
//	 *
//	 *         400: description: Malformed object in the query
//	 * 
//	 *         401: description: The client is not authorized for this operation
//	 */
//	@PUT
//	@Path("questionnaire/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response deleteQuestionnaire(@PathParam(value = "id") String id, String jsonContent) {
//
//		if ((jsonContent == null) || (jsonContent.length() == 0)) {
//    		logger.error("Null or empty content received, returning BAD REQUEST response");
//    		return Response.status(Status.BAD_REQUEST).build();
//    	}
//		QuestionnairesService service = new QuestionnairesService();
//		service.createOrReplaceQuestionnaire(id,jsonContent);
//		service.close();
//		logger.info("Questionnaire "+ id +" created");
//		return Response.status(Status.OK).build();
//
//	}
//	
	

	/**
	 * Creates a new `Questionnaire`
	 * 
	 * @param name:
	 *            questionnaire in: body description: The new questionnaire to
	 *            create (required: true)
	 * 
	 * @param schema:
	 *            $ref: '#/definitions/Questionnaire'
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
	public Response createQuestionnaire(String jsonContent) {
		if ((jsonContent == null) || (jsonContent.length() == 0)) {
    		logger.error("Null or empty content received, returning BAD REQUEST response");
    		return Response.status(Status.BAD_REQUEST).build();
    	}
		QuestionnairesService service = new QuestionnairesService();
		String id = service.createQuestionnaire(jsonContent);
		//TODO return a generic uri
		String uriQuestionnaire = "http://dvrmspogfolht01.ad.insee.intra/rmspogfo/pogues/persistence/questionnaire/"+id;
		service.close();
		logger.info("New questionnaire created , uri :" + uriQuestionnaire);
		return Response.status(Status.CREATED).header("Location", uriQuestionnaire).build();	
	}

	/**
	 * Creates or replaces the `QuestionnaireList` object.
	 * 
	 * @param name:
	 *            list in: body description: List of questionnaires to save
	 *            required: true
	 *
	 * @param schema:
	 *            $ref: '#/definitions/QuestionnaireList'
	 * 
	 * @return 501: description: Not implemented
	 */
	@PUT
	@Path("questionnaires")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrReplaceQuestionnaireList(String jsonContent) {

		return Response.status(Status.NOT_IMPLEMENTED).build();

	}


}
