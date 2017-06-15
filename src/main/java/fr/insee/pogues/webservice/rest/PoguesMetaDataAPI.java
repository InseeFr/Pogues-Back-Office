package fr.insee.pogues.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

/**
 * Main WebService class of the RMeSAPI service
 * 
 * @author I6VWID
 *
 */
@Path("/meta-data")
public class PoguesMetaDataAPI {

	final static Logger logger = Logger.getLogger(PoguesMetaDataAPI.class);

	
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
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@POST
	@Path("questionnaire")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response pubishQuestionnaire(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("questionnaires/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionnairesByOwner(@PathParam(value = "owner") String owner){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("questionnaires/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionnairesByLabel(@PathParam(value = "label") String label){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("sequence/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getSequenceOrSubSequence(@PathParam(value = "id") String id){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("sequences/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getSequencesOrSubSequencesByOwner(@PathParam(value = "owner") String owner){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("sequences/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getSequencesOrSubSequencesByLabel(@PathParam(value = "label") String label){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("question/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestion(@PathParam(value = "id") String id){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("questions/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionsByOwner(@PathParam(value = "owner") String owner){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("questions/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionsByLabel(@PathParam(value = "label") String label){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("codesList/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getCodesList(@PathParam(value = "id") String id){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	@GET
	@Path("codesLists/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getCodesListsByOwner(@PathParam(value = "owner") String owner){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getCodesListsByCriteria(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getCodesListByCriteria(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedSequenceOrSubSequence(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedSequencesOrSubSequenceByCriteria(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedCodesLists(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedCodesListsByCriteria(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedCodesListByCriteria(){		
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedQuestion(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	public Response getMutualizedQuestionsByCriteria(){
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
}