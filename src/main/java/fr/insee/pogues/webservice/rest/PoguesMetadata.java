package fr.insee.pogues.webservice.rest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.transforms.reuse.DDIToPoguesXMLCodeList;
import fr.insee.pogues.transforms.visualize.PoguesXMLToPoguesJSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Main WebService class of the MetaData service
 *
 * @author I6VWID
 */
@RestController
@RequestMapping("/api/meta-data")
@Tag(name = "Pogues MetaData API")
@SecurityRequirement(name = "bearerAuth")
public class PoguesMetadata {

	final static Logger logger = LogManager.getLogger(PoguesMetadata.class);

	@Autowired
	MetadataService metadataService;

	@Autowired
	DDIToPoguesXMLCodeList ddiToXML;

	@Autowired
	PoguesXMLToPoguesJSON xmlToJSON;

	@GET
	@GetMapping("item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getItem", summary = "Gets the item with id {id}", description = "Get an item from Colectica Repository, given it's {id}", responses = {
			@ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = ColecticaItem.class))) })
	public ResponseEntity<Object> getItem(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItem item = metadataService.getItem(id);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@GetMapping("item/{id}/refs/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getChildrenRef", summary = "Get the children refs with parent id {id}", description = "This will give a list of object containing a reference id, version and agency. Note that you will"
			+ "need to map response objects keys to be able to use it for querying items "
			+ "(see /items doc model)", responses = {
					@ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = ColecticaItemRefList.class))) })
	public ResponseEntity<Object> getChildrenRef(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItemRefList refs = metadataService.getChildrenRef(id);
			return ResponseEntity.status(HttpStatus.OK).body(refs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@GetMapping("units")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getUnits", summary = "Get units measure", description = "This will give a list of objects containing the uri and the label for all units", responses = {
			@ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(type = "List", implementation = Unit.class))) })
	public ResponseEntity<Object> getUnits() throws Exception {
		List<Unit> units = new ArrayList<>();
		try {
			units = metadataService.getUnits();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.OK).body(units);
	}

	@POST
	@PostMapping("items")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "getItems",
			summary = "Get all de-referenced items", 
			description = "Maps a list of ColecticaItemRef given as a payload to a list of actual full ColecticaItem objects",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(type = "List", implementation = ColecticaItem.class)))}
			)
	public ResponseEntity<Object> getItems(
			@Parameter(description = "Item references query object", required = true) ColecticaItemRefList query)
			throws Exception {
		try {
			List<ColecticaItem> children = metadataService.getItems(query);
			return ResponseEntity.status(HttpStatus.OK).body(children);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@GetMapping("item/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@Operation(operationId = "getFullDDI", summary = "Get DDI document", description = "Gets a full DDI document from Colectica repository reference {id}"
	/* ,response = String.class */)
	public ResponseEntity<Object> getFullDDI(@PathParam(value = "id") String id) throws Exception {
		try {
			String ddiDocument = metadataService.getDDIDocument(id);
			StreamingOutput stream = output -> {
				try {
					output.write(ddiDocument.getBytes(StandardCharsets.UTF_8));
				} catch (Exception e) {
					throw new PoguesException(500, "Transformation error", e.getMessage());
				}
			};
			return ResponseEntity.status(HttpStatus.OK).body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	//
	// @GET
	// @Path("questionnaire/{id}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getQuestionnaire",
	// notes = "Gets the questionnaire with id {id}",
	// response = String.class)
	// public Response getQuestionnaire(@PathParam(value = "id") String id)
	// throws Exception {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @POST
	// @Path("questionnaire")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "pubishQuestionnaire",
	// notes = "Puts the questionnaire : create a new questionnaire or a new
	// version of the questionnaire on the repository",
	// response = String.class)
	// public Response pubishQuestionnaire() {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }
	//
	// @GET
	// @Path("questionnaires/owner/{owner}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getQuestionnairesByOwner",
	// notes = "Gets the questionnaires list with owner {owner}",
	// response = String.class)
	// public Response getQuestionnairesByOwner(@PathParam(value = "owner")
	// String owner) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("questionnaires/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getQuestionnairesByLabel",
	// notes = "Gets questionnaires list with label {label}",
	// response = String.class)
	// public Response getQuestionnairesByLabel(@PathParam(value = "label")
	// String label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("sequence/{id}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getSequenceOrSubSequence",
	// notes = "Gets the Sequence or SubSequence with id {id}",
	// response = String.class)
	// public Response getSequenceOrSubSequence(@PathParam(value = "id") String
	// id) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("sequences/owner/{owner}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getSequencesOrSubSequencesByOwner",
	// notes = "Gets Sequences or SubSequences list with owner {owner}",
	// response = String.class)
	// public Response getSequencesOrSubSequencesByOwner(@PathParam(value =
	// "owner") String owner) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("sequences/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getSequencesOrSubSequencesByLabel",
	// notes = "Gets Sequences or SubSequences list with label {label}",
	// response = String.class)
	// public Response getSequencesOrSubSequencesByLabel(@PathParam(value =
	// "label") String label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("question/{id}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getQuestion",
	// notes = "Gets the question with id {id}",
	// response = String.class)
	// public Response getQuestion(@PathParam(value = "id") String id) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("questions/owner/{owner}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getQuestionsByOwner",
	// notes = "Gets questions list with owner {owner}",
	// response = String.class)
	// public Response getQuestionsByOwner(@PathParam(value = "owner") String
	// owner) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

	// @GET
	// @Path("questions/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getQuestionsByLabel",
	// notes = "Gets questions list with label {label}",
	// response = String.class)
	// public Response getQuestionsByLabel(@PathParam(value = "label") String
	// label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }
	//

//	@GET
//	@GetMapping("code-list/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Operation(operationId = "getCodeList", summary = "getCodeList", description = "Gets the code-list with id {id}"
//	/* ,response = String.class */)
//	public ResponseEntity<Object> getCodeList(@PathParam(value = "id") String id) throws Exception {
//		String codeList = metadataService.getCodeList(id);
//		PipeLine pipeline = new PipeLine();
//		Map<String, Object> params = new HashMap<>();
//		try {
//			StreamingOutput stream = output -> {
//				try {
//					output.write(pipeline.from(codeList).map(ddiToXML::transform, params,"codeList")
//							.map(xmlToJSON::transform, params,"codeList").transform().getBytes());
//				} catch (Exception e) {
//					logger.error(e.getMessage());
//					throw new PoguesException(500, e.getMessage(), null);
//				}
//			};
//			return ResponseEntity.status(HttpStatus.OK).body(stream);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			throw e;
//		}
//	}

	//
	// @GET
	// @Path("codesLists/owner/{owner}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getCodesListsByOwner",
	// notes = "Gets codesLists list with owner {owner}",
	// response = String.class)
	// public Response getCodesListsByOwner(@PathParam(value = "owner") String
	// owner) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }
	//
	// @GET
	// @Path("codesLists/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getCodesListsByLabel",
	// notes = "Gets codesLists list with label {label}",
	// response = String.class)
	// public Response getCodesListsByLabel(@PathParam(value = "label") String
	// label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }
	//
	//
	// @GET
	// @Path("mutualizedSequencesOrSubSequences/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getMutualizedSequencesOrSubSequencesByLabel",
	// notes = "Gets Mutualized Sequences or SubSequences list with label
	// {label}",
	// response = String.class)
	// public Response
	// getMutualizedSequencesOrSubSequencesByLabel(@PathParam(value = "label")
	// String label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }
	//
	//
	// @GET
	// @Path("mutualizedCodesLists/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getMutualizedCodesListsByLabel",
	// notes = "Gets Mutualized CodesLists list with label {label}",
	// response = String.class)
	// public Response getMutualizedCodesListsByLabel(@PathParam(value =
	// "label") String label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }
	//
	// @GET
	// @Path("mutualizedQuestion/label/{label}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @ApiOperation(value = "getMutualizedQuestionsByLabel",
	// notes = "Gets Mutualized Questions list with label {label}",
	// response = String.class)
	// public Response getMutualizedQuestionsByLabel(@PathParam(value = "label")
	// String label) {
	// return Response.status(Status.NOT_IMPLEMENTED).build();
	// }

}