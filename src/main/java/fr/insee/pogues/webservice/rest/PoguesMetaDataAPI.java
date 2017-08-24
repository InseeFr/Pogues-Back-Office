package fr.insee.pogues.webservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Main WebService class of the MetaData service
 *
 * @author I6VWID
 *
 */
@Path("/meta-data")
@Api(value = "Pogues MetaData API")
public class PoguesMetaDataAPI {

    final static Logger logger = LogManager.getLogger(PoguesMetaDataAPI.class);


    @GET
    @Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionnaire",
            notes = "Gets the questionnaire with id {id}",
            response = String.class)
    public Response getQuestionnaire(@PathParam(value = "id") String id) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("questionnaire")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "pubishQuestionnaire",
            notes = "Puts the questionnaire : create a new questionnaire or a new version of the questionnaire on the repository",
            response = String.class)
    public Response pubishQuestionnaire(){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questionnaires/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionnairesByOwner",
            notes = "Gets the questionnaires list with owner {owner}",
            response = String.class)
    public Response getQuestionnairesByOwner(@PathParam(value = "owner") String owner){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questionnaires/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionnairesByLabel",
            notes = "Gets questionnaires list with label {label}",
            response = String.class)
    public Response getQuestionnairesByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("sequence/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getSequenceOrSubSequence",
            notes = "Gets the Sequence or SubSequence with id {id}",
            response = String.class)
    public Response getSequenceOrSubSequence(@PathParam(value = "id") String id){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("sequences/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getSequencesOrSubSequencesByOwner",
            notes = "Gets Sequences or SubSequences list with owner {owner}",
            response = String.class)
    public Response getSequencesOrSubSequencesByOwner(@PathParam(value = "owner") String owner){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("sequences/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getSequencesOrSubSequencesByLabel",
            notes = "Gets Sequences or SubSequences list with label {label}",
            response = String.class)
    public Response getSequencesOrSubSequencesByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("question/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestion",
            notes = "Gets the question with id {id}",
            response = String.class)
    public Response getQuestion(@PathParam(value = "id") String id){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questions/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionsByOwner",
            notes = "Gets questions list with owner {owner}",
            response = String.class)
    public Response getQuestionsByOwner(@PathParam(value = "owner") String owner){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questions/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionsByLabel",
            notes = "Gets questions list with label {label}",
            response = String.class)
    public Response getQuestionsByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("codesList/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getCodesList",
            notes = "Gets the codesList with id {id}",
            response = String.class)
    public Response getCodesList(@PathParam(value = "id") String id){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("codesLists/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getCodesListsByOwner",
            notes = "Gets codesLists list with owner {owner}",
            response = String.class)
    public Response getCodesListsByOwner(@PathParam(value = "owner") String owner){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("codesLists/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getCodesListsByLabel",
            notes = "Gets codesLists list with label {label}",
            response = String.class)
    public Response getCodesListsByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }


    @GET
    @Path("mutualizedSequencesOrSubSequences/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getMutualizedSequencesOrSubSequencesByLabel",
            notes = "Gets Mutualized Sequences or SubSequences list with label {label}",
            response = String.class)
    public Response getMutualizedSequencesOrSubSequencesByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }


    @GET
    @Path("mutualizedCodesLists/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getMutualizedCodesListsByLabel",
            notes = "Gets Mutualized CodesLists list with label {label}",
            response = String.class)
    public Response getMutualizedCodesListsByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("mutualizedQuestion/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getMutualizedQuestionsByLabel",
            notes = "Gets Mutualized Questions list with label {label}",
            response = String.class)
    public Response getMutualizedQuestionsByLabel(@PathParam(value = "label") String label){
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

}