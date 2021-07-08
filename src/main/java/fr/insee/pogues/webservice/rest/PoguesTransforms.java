package fr.insee.pogues.webservice.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.DDIToFO;
import fr.insee.pogues.transforms.DDIToODT;
import fr.insee.pogues.transforms.DDIToXForm;
import fr.insee.pogues.transforms.FOToPDF;
import fr.insee.pogues.transforms.JSONToXML;
import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.PoguesXMLToDDI;
import fr.insee.pogues.transforms.Transformer;
import fr.insee.pogues.transforms.XFormToURI;
import fr.insee.pogues.transforms.XFormsToXFormsHack;
import fr.insee.pogues.transforms.XMLToJSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Main WebService class of the PoguesBOOrchestrator
 *
 * @author I6VWID
 */
@Path("/transform")
@Tag(name = "Pogues Transforms")
public class PoguesTransforms {

	final static Logger logger = LogManager.getLogger(PoguesTransforms.class);

	@Autowired
	JSONToXML jsonToXML;

	@Autowired
	XMLToJSON xmlToJson;

	@Autowired
	PoguesXMLToDDI poguesXMLToDDI;

	@Autowired
	DDIToXForm ddiToXForm;

	@Autowired
	DDIToODT ddiToOdt;
	
	@Autowired
	DDIToFO ddiToFo;
	
	@Autowired
	FOToPDF foToPdf;

	@Autowired
	XFormToURI xformToUri;

	@Autowired
	XFormsToXFormsHack xformToXformsHack;

	@Autowired
	QuestionnairesService questionnairesService;

	@POST
	@Path("visualize/{dataCollection}/{questionnaire}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	@Operation(
			summary = "Get visualization URI from JSON serialized Pogues entity", 
			description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
	@RequestBody(
			description = "JSON representation of the Pogues Model",
			content = @Content(mediaType = "application/json")
			)
	public Response visualizeFromBody(@Context final HttpServletRequest request,
			@PathParam(value = "dataCollection") String dataCollection,
			@PathParam(value = "questionnaire") String questionnaire) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("dataCollection", dataCollection.toLowerCase());
		params.put("questionnaire", questionnaire.toLowerCase());
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream())
							.map(jsonToXML::transform, params, questionnaire.toLowerCase())
							.map(poguesXMLToDDI::transform, params, questionnaire.toLowerCase())
							.map(ddiToXForm::transform, params, questionnaire.toLowerCase())
							.map(xformToXformsHack::transform, params, questionnaire.toLowerCase())
							.map(xformToUri::transform, params, questionnaire.toLowerCase()).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			return Response.ok(stream).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("visualize-from-ddi/{dataCollection}/{questionnaire}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@Operation(
			summary = "Get visualization URI from DDI questionnaire", 
			description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
	@RequestBody(
			description = "DDI representation of the Pogues Model",
			content = @Content(mediaType = "application/xml")
			)
	public Response visualizeFromDDIBody(@Context final HttpServletRequest request,
			@PathParam(value = "dataCollection") String dataCollection,
			@PathParam(value = "questionnaire") String questionnaire) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("dataCollection", dataCollection.toLowerCase());
		params.put("questionnaire", questionnaire.toLowerCase());
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream())
							.map(ddiToXForm::transform, params, questionnaire.toLowerCase())
							.map(xformToXformsHack::transform, params, questionnaire.toLowerCase())
							.map(xformToUri::transform, params, questionnaire.toLowerCase()).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			return Response.ok(stream).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("visualize-spec")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Operation(
			summary = "Get visualization spec from JSON serialized Pogues entity", hidden = true)
	@RequestBody(
			description = "JSON representation of the Pogues Model",
			content = @Content(mediaType = "application/json")
			)
	public Response visualizeSpecFromBody(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String questionnaireName = "spec";
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(
							pipeline.from(request.getInputStream()).map(jsonToXML::transform, params, questionnaireName)
									.map(poguesXMLToDDI::transform, params, questionnaireName)
									.map(ddiToOdt::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=form.fodt").build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("visualize-ddi")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Operation(summary = "Get visualization DDI file from JSON serialized Pogues entity")
	@RequestBody(
			description = "JSON representation of the Pogues Model",
			content = @Content(mediaType = "application/json")
			)
	public Response visualizeDDIFromBody(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String questionnaireName = "ddi";
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(
							pipeline.from(request.getInputStream()).map(jsonToXML::transform, params, questionnaireName)
									.map(poguesXMLToDDI::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=form.xml").build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("visualize-pdf")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Operation(summary = "Get visualization PDF questionnaire from JSON serialized Pogues entity", hidden = true)
	@RequestBody(
			description = "JSON representation of the Pogues Model",
			content = @Content(mediaType = "application/json")
			)
	public Response visualizePDFFromBody(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String filePath = null;
		String questionnaireName = "pdf";
		try {
			filePath = pipeline.from(request.getInputStream()).map(jsonToXML::transform, params, questionnaireName)
					.map(poguesXMLToDDI::transform, params, questionnaireName)
					.map(ddiToFo::transform, params, questionnaireName)
					.map(foToPdf::transform, params, questionnaireName).transform();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new PoguesException(500, e.getMessage(), null);
		}

		File file = new File(filePath);
		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();

	}

	@POST
	@Path("ddi2pdf")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Operation(summary = "Get visualization PDF questionnaire from DDI questionnaire")
	@RequestBody(
			description = "DDI representation of the questionnaire",
			content = @Content(mediaType = "application/xml")
			)
	@Parameters(value= {
			@Parameter(
					in = ParameterIn.QUERY, 
					name = "columns", 
					description="Columns",
					schema=@Schema(type="string",allowableValues = {"1","2","3","4"})),
			@Parameter(
					in = ParameterIn.QUERY, 
					name = "orientation", 
					description="Orientation",
					schema=@Schema(type="string",allowableValues = {"0","90"})),
			@Parameter(
					in = ParameterIn.QUERY, 
					name = "capture", 
					description="Capture",
					schema=@Schema(type="string",allowableValues = {"optical","manual"})),
			@Parameter(
					in = ParameterIn.QUERY, 
					name = "studyunit", 
					description="StudyUnit",
					schema=@Schema(type="string",allowableValues = {"business","household","default"})),
			@Parameter(
					in = ParameterIn.QUERY, 
					name = "timequestion", 
					description="TimeQuestion",
					schema=@Schema(type="string",allowableValues = {"true","false"}))
	})
	public Response ddi2pdfWithParamTest(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		if (request.getParameter("columns") != null) {
			params.put("columns", request.getParameter("columns"));
		}
		if (request.getParameter("orientation") != null) {
			params.put("orientation", request.getParameter("orientation"));
		}
		if (request.getParameter("capture") != null) {
			params.put("capture", request.getParameter("capture"));
		}
		if (request.getParameter("studyunit") != null) {
			params.put("studyunit", request.getParameter("studyunit"));
		}
		if (request.getParameter("timequestion") != null) {
			params.put("timequestion", request.getParameter("timequestion"));
		}
		String filePath = null;
		String questionnaireName = "pdf";

		try {
			filePath = pipeline.from(request.getInputStream()).map(ddiToFo::transform, params, questionnaireName)
					.map(foToPdf::transform, params, questionnaireName)
					.transform();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new PoguesException(500, e.getMessage(), null);
		}

		File file = new File(filePath);
		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();

	}
	
	@POST
	@Path("fo2pdf")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Operation(summary = "Get visualization PDF questionnaire from FO questionnaire")
	@RequestBody(
			description = "FO representation of the Pogues Model",
			content = @Content(mediaType = "application/xml")
			)
	public Response fo2Pdf (@Context final HttpServletRequest request) throws Exception{
		String filePath = null;
		String questionnaireName = "pdf";
		
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			filePath = pipeline.from(request.getInputStream()).map(foToPdf::transform, params, questionnaireName)
					.transform();;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		File file = new File(filePath);
		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();
	}

	@POST
	@Path("json2xml")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Get Pogues XML From Pogues JSON", 
			description = "Returns a serialized XML based on a JSON entity that must comply with Pogues data model")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"), 
			@ApiResponse(responseCode = "500", description = "Error") 
			})
	@RequestBody(
			description = "JSON representation of the Pogues Model",
			content = @Content(mediaType = "application/json")
			)
	public Response json2XML(@Context final HttpServletRequest request) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(request, jsonToXML, questionnaire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("xml2json")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_XML)
	@Operation(
			summary = "Get Pogues JSON From Pogues XML", 
			description = "Returns a JSON entity that must comply with Pogues data model based on XML")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"), 
			@ApiResponse(responseCode = "500", description = "Error") })
	@RequestBody(
			description = "XML representation of the Pogues Model",
			content = @Content(mediaType = "application/xml")
			)
	public Response xml2Json(@Context final HttpServletRequest request) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(request, xmlToJson, questionnaire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			System.out.println(e);
			throw e;
		}
	}

	@POST
	@Path("xform2uri/{dataCollection}/{questionnaire}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	@Operation(
			summary = "Get Pogues visualization URI From Pogues XForm document", 
			description = "Returns the vizualisation URI of a form that was generated from XForm description found in body")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"), 
			@ApiResponse(responseCode = "500", description = "Error") })
	@RequestBody(
			description = "XForm document generated from DDI metadata",
			content = @Content(mediaType = "text/plain")
			)
	public String xForm2URI(@Context final HttpServletRequest request,
			@PathParam(value = "dataCollection") String dataCollection,
			@PathParam(value = "questionnaire") String questionnaire) throws Exception {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("dataCollection", dataCollection.toLowerCase());
			params.put("questionnaire", questionnaire.toLowerCase());
			String input = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
			return xformToUri.transform(input, params, questionnaire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private Response transform(HttpServletRequest request, Transformer transformer, String questionnaire)
			throws Exception {

		StreamingOutput stream = output -> {
			try {
				transformer.transform(request.getInputStream(), output, null, questionnaire);
			} catch (Exception e) {
				e.printStackTrace();
				throw new PoguesException(500, "Transformation error", e.getMessage());
			}
		};
		return Response.ok(stream).build();
	}

	private Response transform(HttpServletRequest request, String string) throws Exception {
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				try {
					Writer writer = new BufferedWriter(new OutputStreamWriter(os));
					writer.write(string);
					writer.flush();
				} catch (Exception e) {
					throw new PoguesException(500, "Transformation error", e.getMessage());
				}

			}
		};
		return Response.ok(stream).build();
	}
}
