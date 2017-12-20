package fr.insee.pogues.webservice.rest;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.DDIToODT;
import fr.insee.pogues.transforms.DDIToXForm;
import fr.insee.pogues.transforms.JSONToXML;
import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.Transformer;
import fr.insee.pogues.transforms.XFormToURI;
import fr.insee.pogues.transforms.XFormsToXFormsHack;
import fr.insee.pogues.transforms.XMLToDDI;
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
	XMLToDDI xmlToDDI;

	@Autowired
	DDIToXForm ddiToXForm;
	
	@Autowired
	DDIToODT ddiToOdt;

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
					output.write(pipeline.from(request.getInputStream()).map(jsonToXML::transform, params)
							.map(xmlToDDI::transform, params).map(ddiToXForm::transform, params)
							.map(xformToXformsHack::transform, params).map(xformToUri::transform, params).transform()
							.getBytes());
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
			String uri = pipeline.from(input).map(jsonToXML::transform, params).map(xmlToDDI::transform, params)
					.map(ddiToXForm::transform, params).map(xformToXformsHack::transform, params)
					.map(xformToUri::transform, params).transform();
			return Response.seeOther(URI.create(uri)).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			input.close();
		}
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
		try {
			StreamingOutput stream = output -> {
				try {
					output.write(pipeline.from(request.getInputStream()).map(jsonToXML::transform, params)
							.map(xmlToDDI::transform, params).map(ddiToOdt::transform, params)
							.transform()
							.getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			
			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=form.fodt")
					.build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("visualize-spec/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get specification odt file from JSON serialized Pogues entity", notes = "Retrieves entity in datastore before pass it through the transformation pipeline")
	public Response visualizeSpecFromId(@Context final HttpServletRequest request, @PathParam(value = "id") String id) throws Exception {
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
					output.write(pipeline.from(input).map(jsonToXML::transform, params)
							.map(xmlToDDI::transform, params).map(ddiToOdt::transform, params)
							.transform()
							.getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
				finally {
					input.close();
				}
			};
			
			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=form.fodt")
					.build();
			
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
		try {
			// JSON : request.getInputStream()
			File file = new File(getClass().getClassLoader().getResource("pdf/test.pdf").getPath());
			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("visualize-pdf/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get visualization PDF questionnaire from JSON serialized Pogues entity", notes = "Retrieves entity in datastore before pass it through the transformation pipeline")
	public Response visualizePDFFromId(@PathParam(value = "id") String id) throws Exception {
		try {

			File file = new File(getClass().getClassLoader().getResource("pdf/test.pdf").getPath());
			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
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
		try {
			return transform(request, jsonToXML);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("xml2ddi")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI From Pogues XML", notes = "Returns a DDI standard compliant document based on a XML representation of a Pogues data model entity")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Error") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "xml body", value = "XML representation of the Pogues Model", paramType = "body", dataType = "string") })
	public Response xml2DDi(@Context final HttpServletRequest request) throws Exception {
		try {
			return transform(request, xmlToDDI);
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
		try {
			return transform(request, ddiToXForm);
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
		try {
			return transform(request, xformToXformsHack);
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
			return xformToUri.transform(input, params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private Response transform(HttpServletRequest request, Transformer transformer) throws Exception {
		StreamingOutput stream = output -> {
			try {
				transformer.transform(request.getInputStream(), output, null);
			} catch (Exception e) {
				throw new PoguesException(500, "Transformation error", e.getMessage());
			}
		};
		return Response.ok(stream).build();
	}
	
	private Response transform(HttpServletRequest request, String string) throws Exception {
		StreamingOutput stream = new StreamingOutput() {
		    @Override
		    public void write(OutputStream os) throws IOException,
		    WebApplicationException {
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
