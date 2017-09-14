package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.service.MetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

/**
 * Main WebService class of the MetaData service
 *
 * @author I6VWID
 */
@Path("/meta-data")
@Api(value = "Pogues MetaData API")
public class PoguesMetaDataAPI {

    final static Logger logger = LogManager.getLogger(PoguesMetaDataAPI.class);

    @Autowired
    MetadataService metadataService;


    @GET
    @Path("item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getItem",
            notes = "Gets the item with id {id}",
            response = ColecticaItem.class)
    public Response getItem(@PathParam(value = "id") String id) throws Exception {
        try {
            ColecticaItem item = metadataService.getItem(id);
            return Response.ok().entity(item).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @GET
    @Path("item/{id}/refs/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getItems",
            notes = "Gets the chidlren refs with parent id {id}",
            response = ColecticaItemRefList.class)
    public Response getChildrenRef(@PathParam(value = "id") String id) throws Exception {
        try {
            ColecticaItemRefList refs = metadataService.getChildrenRef(id);
            return Response.ok().entity(refs).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    @POST
    @Path("items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getItems",
            notes = "Map query object references to actual colectica items",
            response = ColecticaItem.class,
            responseContainer = "List"
    )
    public Response getChildren(
            @ApiParam(value = "Item references query object", required = true) ColecticaItemRefList query
    ) throws Exception {
        try {
            List<ColecticaItem> children = metadataService.getItems(query);
            return Response.ok().entity(children).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    @GET
    @Path("item/{id}/ddi/")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "getItems",
            notes = "Gets the chidlren refs with parent id {id}",
            response = ColecticaItemRefList.class)
    public Response getFullDDI(@PathParam(value = "id") String id) throws Exception {
        try {
            String ddiDocument = metadataService.getDDIDocument(id);
            return Response.ok().entity(ddiDocument).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    @GET
    @Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionnaire",
            notes = "Gets the questionnaire with id {id}",
            response = String.class)
    public Response getQuestionnaire(@PathParam(value = "id") String id) throws Exception {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("questionnaire")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "pubishQuestionnaire",
            notes = "Puts the questionnaire : create a new questionnaire or a new version of the questionnaire on the repository",
            response = String.class)
    public Response pubishQuestionnaire() {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questionnaires/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionnairesByOwner",
            notes = "Gets the questionnaires list with owner {owner}",
            response = String.class)
    public Response getQuestionnairesByOwner(@PathParam(value = "owner") String owner) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questionnaires/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionnairesByLabel",
            notes = "Gets questionnaires list with label {label}",
            response = String.class)
    public Response getQuestionnairesByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("sequence/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getSequenceOrSubSequence",
            notes = "Gets the Sequence or SubSequence with id {id}",
            response = String.class)
    public Response getSequenceOrSubSequence(@PathParam(value = "id") String id) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("sequences/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getSequencesOrSubSequencesByOwner",
            notes = "Gets Sequences or SubSequences list with owner {owner}",
            response = String.class)
    public Response getSequencesOrSubSequencesByOwner(@PathParam(value = "owner") String owner) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("sequences/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getSequencesOrSubSequencesByLabel",
            notes = "Gets Sequences or SubSequences list with label {label}",
            response = String.class)
    public Response getSequencesOrSubSequencesByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("question/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestion",
            notes = "Gets the question with id {id}",
            response = String.class)
    public Response getQuestion(@PathParam(value = "id") String id) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questions/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionsByOwner",
            notes = "Gets questions list with owner {owner}",
            response = String.class)
    public Response getQuestionsByOwner(@PathParam(value = "owner") String owner) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("questions/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getQuestionsByLabel",
            notes = "Gets questions list with label {label}",
            response = String.class)
    public Response getQuestionsByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("codesList/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getCodesList",
            notes = "Gets the codesList with id {id}",
            response = String.class)
    public Response getCodesList(@PathParam(value = "id") String id) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("codesLists/owner/{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getCodesListsByOwner",
            notes = "Gets codesLists list with owner {owner}",
            response = String.class)
    public Response getCodesListsByOwner(@PathParam(value = "owner") String owner) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("codesLists/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getCodesListsByLabel",
            notes = "Gets codesLists list with label {label}",
            response = String.class)
    public Response getCodesListsByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }


    @GET
    @Path("mutualizedSequencesOrSubSequences/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getMutualizedSequencesOrSubSequencesByLabel",
            notes = "Gets Mutualized Sequences or SubSequences list with label {label}",
            response = String.class)
    public Response getMutualizedSequencesOrSubSequencesByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }


    @GET
    @Path("mutualizedCodesLists/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getMutualizedCodesListsByLabel",
            notes = "Gets Mutualized CodesLists list with label {label}",
            response = String.class)
    public Response getMutualizedCodesListsByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("mutualizedQuestion/label/{label}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "getMutualizedQuestionsByLabel",
            notes = "Gets Mutualized Questions list with label {label}",
            response = String.class)
    public Response getMutualizedQuestionsByLabel(@PathParam(value = "label") String label) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

}