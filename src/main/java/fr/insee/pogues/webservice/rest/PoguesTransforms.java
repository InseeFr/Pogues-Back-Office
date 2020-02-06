package fr.insee.pogues.webservice.rest;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.DDI32ToDDI33;
import fr.insee.pogues.transforms.DDIToFO;
import fr.insee.pogues.transforms.DDIToLunaticXML;
import fr.insee.pogues.transforms.DDIToODT;
import fr.insee.pogues.transforms.DDIToPDF;
import fr.insee.pogues.transforms.DDIToXForm;
import fr.insee.pogues.transforms.FOToPDF;
import fr.insee.pogues.transforms.JSONToXML;
import fr.insee.pogues.transforms.LunaticXMLFToLunaticJSONF;
import fr.insee.pogues.transforms.LunaticXMLToLunaticJSON;
import fr.insee.pogues.transforms.LunaticXMLToLunaticXMLF;
import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.PoguesXMLToDDI;
import fr.insee.pogues.transforms.Transformer;
import fr.insee.pogues.transforms.XFormToURI;
import fr.insee.pogues.transforms.XFormsToXFormsHack;
import fr.insee.pogues.transforms.XMLToJSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Main WebService class of the PoguesBOOrchestrator
 *
 * @author I6VWID
 */
@Path("/transform")
@Api(value = "Pogues Transforms")
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
	DDIToLunaticXML ddiToLunaticXML;

	@Autowired
	LunaticXMLToLunaticXMLF lunaticXMLToLunaticXMLF;

	@Autowired
	LunaticXMLToLunaticJSON lunaticXMLToLunaticJSON;

	@Autowired
	LunaticXMLFToLunaticJSONF lunaticXMLFToLunaticJSONF;
	
	@Autowired
	DDIToFO ddiToFo;
	
	@Autowired
	FOToPDF foToPdf;
	
	@Autowired
	DDIToPDF ddiToPdf;
	
	@Autowired
	DDI32ToDDI33 ddi32Toddi33;

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
	@ApiOperation(value = "Get visualization URI from JSON serialized Pogues entity", notes = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "json body", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "org.json.simple.JSONObject") })
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
	@Path("visualize-capi/{dataCollection}/{questionnaire}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get visualization URI from JSON serialized Pogues entity")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "json body", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "org.json.simple.JSONObject") })
	public Response visualizeCapiFromBody(@Context final HttpServletRequest request,
			@PathParam(value = "dataCollection") String dataCollection,
			@PathParam(value = "questionnaire") String questionnaire) throws Exception {

		JSONParser parser = new JSONParser();
		JSONObject questionnaireJSON = (JSONObject) parser
				.parse(new InputStreamReader(request.getInputStream(), "UTF-8"));
		String id = (String) questionnaireJSON.get("id");
		String uri = "https://pogues-vis.dev.innovation.insee.eu/questionnaire/" + id;
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(uri.getBytes());
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
	@ApiOperation(value = "Get visualization URI from DDI questionnaire", notes = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "ddi body", value = "DDI representation of the questionnaire", paramType = "body") })
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
	@Path("xml-pogues2lunaticflat")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get JSON  Lunatic FLat model from XML Pogues questionnaire")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xml pogues body", value = "XML Pogues representation of the questionnaire", paramType = "body") })
	public Response xmlPogues2lunaticflat(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String survey = "test";
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream()).map(poguesXMLToDDI::transform, params, survey)
							.map(ddiToLunaticXML::transform, params, survey)
							.map(lunaticXMLToLunaticXMLF::transform, params, survey)
							.map(lunaticXMLFToLunaticJSONF::transform, params, survey).transform().getBytes());
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
	@Path("xml-pogues2lunatic")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get JSON  Lunatic FLat model from XML Pogues questionnaire")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xml pogues body", value = "XML Pogues representation of the questionnaire", paramType = "body") })
	public Response xmlPogues2lunatic(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String survey = "test";
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream()).map(poguesXMLToDDI::transform, params, survey)
							.map(ddiToLunaticXML::transform, params, survey)
							.map(lunaticXMLToLunaticJSON::transform, params, survey).transform().getBytes());
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
	@Path("ddi2lunaticflat")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get JSON Lunatic FLat model from DDI questionnaire")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "DDI model body", value = "DDI representation of the questionnaire", paramType = "body") })
	public Response ddi2lunaticflat(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String survey = "test";
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream()).map(ddiToLunaticXML::transform, params, survey)
							.map(lunaticXMLToLunaticXMLF::transform, params, survey)
							.map(lunaticXMLFToLunaticJSONF::transform, params, survey).transform().getBytes());
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

	@GET
	@Path("visualize/{id}")
	@ApiOperation(value = "Get visualization URI from JSON serialized Pogues entity", notes = "Retrieves entity in datastore before pass it through the transformation pipeline")
	public Response visualizeFromId(@PathParam(value = "id") String id) throws Exception {
		PipeLine pipeline = new PipeLine();
		InputStream input = null;
		Map<String, Object> params = new HashMap<>();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			JSONObject dataCollection = (JSONObject) ((JSONArray) questionnaire.get("DataCollection")).get(0);
			String name = ((String) dataCollection.get("id")).toLowerCase();
			params.put("dataCollection", name);
			String questionnaireName = ((String) questionnaire.get("Name")).toLowerCase();
			params.put("questionnaire", questionnaireName);
			input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
			String uri = pipeline.from(input).map(jsonToXML::transform, params, questionnaireName)
					.map(poguesXMLToDDI::transform, params, questionnaireName)
					.map(ddiToXForm::transform, params, questionnaireName)
					.map(xformToXformsHack::transform, params, questionnaireName)
					.map(xformToUri::transform, params, questionnaireName).transform();
			return Response.seeOther(URI.create(uri)).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			input.close();
		}
	}

	@GET
	@Path("visualize-capi/{id}")
	@ApiOperation(value = "Get visualization CAPI URI from questionnaire id (Pogues entity)")
	public Response visualizeCapiFromId(@PathParam(value = "id") String id) throws Exception {
		String uri = "https://pogues-vis.dev.innovation.insee.eu/questionnaire/" + id;
		return Response.seeOther(URI.create(uri)).build();
	}

	@POST
	@Path("visualize-spec")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get visualization spec from JSON serialized Pogues entity")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "json body", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "org.json.simple.JSONObject") })
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
	@ApiOperation(value = "Get visualization DDI file from JSON serialized Pogues entity")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "json body", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "org.json.simple.JSONObject") })
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

	@GET
	@Path("visualize-spec/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get specification odt file from JSON serialized Pogues entity", notes = "Retrieves entity in datastore before pass it through the transformation pipeline")
	public Response visualizeSpecFromId(@Context final HttpServletRequest request, @PathParam(value = "id") String id)
			throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			JSONObject dataCollection = (JSONObject) ((JSONArray) questionnaire.get("DataCollection")).get(0);
			String name = ((String) dataCollection.get("id")).toLowerCase();
			params.put("dataCollection", name);
			String questionnaireName = ((String) questionnaire.get("Name")).toLowerCase();
			params.put("questionnaire", questionnaireName);

			StreamingOutput stream = output -> {
				InputStream input = null;
				try {
					input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
					output.write(pipeline.from(input).map(jsonToXML::transform, params, questionnaireName)
							.map(poguesXMLToDDI::transform, params, questionnaireName)
							.map(ddiToOdt::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				} finally {
					input.close();
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=form.fodt").build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("visualize-ddi/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get DDI file from JSON serialized Pogues entity", notes = "Retrieves entity in datastore before pass it through the transformation pipeline")
	public Response visualizeDDIFromId(@Context final HttpServletRequest request, @PathParam(value = "id") String id)
			throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			JSONObject dataCollection = (JSONObject) ((JSONArray) questionnaire.get("DataCollection")).get(0);
			String name = ((String) dataCollection.get("id")).toLowerCase();
			params.put("dataCollection", name);
			String questionnaireName = ((String) questionnaire.get("Name")).toLowerCase();
			params.put("questionnaire", questionnaireName);

			StreamingOutput stream = output -> {
				InputStream input = null;
				try {
					input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
					output.write(pipeline.from(input).map(jsonToXML::transform, params, questionnaireName)
							.map(poguesXMLToDDI::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				} finally {
					input.close();
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
	@ApiOperation(value = "Get visualization PDF questionnaire from JSON serialized Pogues entity")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "json body", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "org.json.simple.JSONObject") })
	public Response visualizePDFFromBody(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String filePath = null;
		String questionnaireName = "pdf";
		try {
			filePath = pipeline.from(request.getInputStream()).map(jsonToXML::transform, params, questionnaireName)
					.map(poguesXMLToDDI::transform, params, questionnaireName)
					.map(ddiToPdf::transform, params, questionnaireName).transform();			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new PoguesException(500, e.getMessage(), null);
		}

		File file = new File(filePath);
		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();

	}

	@GET
	@Path("visualize-pdf/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get visualization PDF questionnaire from JSON serialized Pogues entity", notes = "Retrieves entity in datastore before pass it through the transformation pipeline")
	public Response visualizePDFFromId(@PathParam(value = "id") String id) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			JSONObject dataCollection = (JSONObject) ((JSONArray) questionnaire.get("DataCollection")).get(0);
			String name = ((String) dataCollection.get("id")).toLowerCase();
			params.put("dataCollection", name);
			String questionnaireName = ((String) questionnaire.get("Name")).toLowerCase();
			params.put("questionnaire", questionnaireName);

			StreamingOutput stream = output -> {
				InputStream input = null;
				try {
					input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
					output.write(pipeline.from(input).map(jsonToXML::transform, params, questionnaireName)
							.map(poguesXMLToDDI::transform, params, questionnaireName)
							.map(ddiToFo::transform, params, questionnaireName)
							.map(foToPdf::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				} finally {
					input.close();
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=form.pdf").build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("ddi2pdf")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get visualization PDF questionnaire from DDI questionnaire")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "ddi body", value = "DDI representation of the questionnaire", paramType = "body", dataType = "string"),
			@ApiImplicitParam(name = "columns", value = "Columns", paramType = "query", dataType = "string",allowableValues="1,2,3,4"),
			@ApiImplicitParam(name = "orientation", value = "Orientation", paramType = "query", dataType = "string",allowableValues="0,90"),
			@ApiImplicitParam(name = "capture", value = "capture", paramType = "query", dataType = "string",allowableValues="optical,manual"),
			@ApiImplicitParam(name = "studyunit", value = "StudyUnit", paramType = "query", dataType = "string",allowableValues="business,household,default"),
			@ApiImplicitParam(name = "timequestion", value = "TimeQuestion", paramType = "query", dataType = "string",allowableValues="true,false")})
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
			filePath = pipeline.from(request.getInputStream()).map(ddiToPdf::transform, params, questionnaireName)
					/*.map(foToPdf::transform, params, questionnaireName)*/
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
	@Path("ddi2fo")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get FO questionnaire from DDI questionnaire")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "ddi body", value = "DDI representation of the questionnaire", paramType = "body", dataType = "string"),
			@ApiImplicitParam(name = "columns", value = "Columns", paramType = "query", dataType = "string",allowableValues="1,2,3,4"),
			@ApiImplicitParam(name = "orientation", value = "Orientation", paramType = "query", dataType = "string",allowableValues="0,90"),
			@ApiImplicitParam(name = "capture", value = "capture", paramType = "query", dataType = "string",allowableValues="optical,manual"),
			@ApiImplicitParam(name = "studyunit", value = "StudyUnit", paramType = "query", dataType = "string",allowableValues="business,household,default"),
			@ApiImplicitParam(name = "timequestion", value = "TimeQuestion", paramType = "query", dataType = "string",allowableValues="true,false")})
	public Response ddi2foWithParamTest(@Context final HttpServletRequest request) throws Exception {
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
		String questionnaireName = "fo";

		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream()).map(ddiToFo::transform, params, questionnaireName)
					.transform().getBytes());
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
	@Path("fo2pdf")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get visualization PDF questionnaire from FO questionnaire")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "FO body", value = "FO representation of the questionnaire", paramType = "body", dataType = "string") })
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
	@ApiOperation(value = "Get Pogues XML From Pogues JSON", notes = "Returns a serialized XML based on a JSON entity that must comply with Pogues data model")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "json body", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "string") })
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
	@ApiOperation(value = "Get Pogues JSON From Pogues XML", notes = "Returns a JSON entity that must comply with Pogues data model based on XML")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xml body", value = "XML representation of the Pogues Model", paramType = "body", dataType = "string") })
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
	@Path("pogues-xml2ddi")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI From Pogues XML", notes = "Returns a DDI standard compliant document based on a Pogues XML representation of a data model entity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xml body", value = "XML representation of the Pogues Model", paramType = "body", dataType = "string") })
	public Response poguesXml2DDi(@Context final HttpServletRequest request) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(request, poguesXMLToDDI, questionnaire);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	@POST
	@Path("ddi2xform")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get Pogues XForm From Pogues DDI metadata", notes = "Returns XForm description based on a DDI standard compliant document describing a Pogues Model entity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "ddi body", value = "DDI representation of the questionnaire", paramType = "body", dataType = "string") })
	public Response ddi2XForm(@Context final HttpServletRequest request) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(request, ddiToXForm, questionnaire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("ddi32Toddi33")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI 3.3 From DDI 3.2")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "ddi body", value = "DDI 3.2 representation of the questionnaire", paramType = "body", dataType = "string") })
	public Response ddi32Toddi33(@Context final HttpServletRequest request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String survey = "test";
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream()).map(ddi32Toddi33::transform, params, survey)
							.transform().getBytes());
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
	@Path("xforms2xformHack")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get XForm (extended) From Xforms", notes = "Returns XForm extended based on a XForms standard")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xforms body", value = "XForms questionnaire", paramType = "body", dataType = "string") })
	public Response ddi2XFormHack(@Context final HttpServletRequest request) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(request, xformToXformsHack, questionnaire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("pogues-json2lunatic-xml/questionnaire/{id}")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get Lunatic XML implementation from JSON serialized Pogues entity id")
	public Response poguesJson2lunaticXML(@PathParam(value = "id") String id) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			String questionnaireName = id;

			StreamingOutput stream = output -> {
				InputStream input = null;
				try {
					input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
					output.write(pipeline.from(input).map(jsonToXML::transform, params, questionnaireName)
							.map(poguesXMLToDDI::transform, params, questionnaireName)
							.map(ddiToLunaticXML::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				} finally {
					input.close();
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_XML).build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("pogues-json2lunatic-json/questionnaire/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get Lunatic JSON implementation from JSON serialized Pogues entity id")
	public Response poguesJson2lunaticJson(@PathParam(value = "id") String id) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			String questionnaireName = id;

			StreamingOutput stream = output -> {
				InputStream input = null;
				try {
					input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
					output.write(pipeline.from(input).map(jsonToXML::transform, params, questionnaireName)
							.map(poguesXMLToDDI::transform, params, questionnaireName)
							.map(ddiToLunaticXML::transform, params, questionnaireName)
							.map(lunaticXMLToLunaticXMLF::transform, params, questionnaireName)
							.map(lunaticXMLFToLunaticJSONF::transform, params, questionnaireName).transform()
							.getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				} finally {
					input.close();
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_JSON).build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("xform2uri/{dataCollection}/{questionnaire}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get Pogues visualization URI From Pogues XForm document", notes = "Returns the vizualisation URI of a form that was generated from XForm description found in body")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xform body", value = "XForm document generated from DDI metadata", paramType = "body", dataType = "string") })
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
